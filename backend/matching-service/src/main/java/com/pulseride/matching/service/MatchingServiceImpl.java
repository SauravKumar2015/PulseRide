package com.pulseride.matching.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pulseride.matching.client.DriverServiceClient;
import com.pulseride.matching.client.RideServiceClient;
import com.pulseride.matching.client.TrackingServiceClient;

import com.pulseride.matching.dto.request.MatchRideRequest;
import com.pulseride.matching.dto.response.DriverCandidateResponse;
import com.pulseride.matching.dto.response.DriverLocationResponse;
import com.pulseride.matching.dto.response.DriverResponse;
import com.pulseride.matching.dto.response.MatchResponse;

import com.pulseride.matching.entity.MatchRecord;
import com.pulseride.matching.entity.MatchStatus;

import com.pulseride.matching.exception.MatchNotFoundException;

import com.pulseride.matching.repository.MatchRecordRepository;

@Service
public class MatchingServiceImpl
        implements MatchingService {

    private final MatchRecordRepository
            matchRecordRepository;

    private final DriverServiceClient
            driverServiceClient;

    private final TrackingServiceClient
            trackingServiceClient;

    private final RideServiceClient
            rideServiceClient;


    private final double searchRadiusKm;

    private final int maxCandidates;

    private final long staleSeconds;


    public MatchingServiceImpl(

            MatchRecordRepository matchRecordRepository,

            DriverServiceClient driverServiceClient,

            TrackingServiceClient trackingServiceClient,

            RideServiceClient rideServiceClient,

            @Value("${matching.search-radius-km}")
            double searchRadiusKm,

            @Value("${matching.max-candidates}")
            int maxCandidates,

            @Value("${matching.candidate-stale-seconds}")
            long staleSeconds) {

        this.matchRecordRepository =
                matchRecordRepository;

        this.driverServiceClient =
                driverServiceClient;

        this.trackingServiceClient =
                trackingServiceClient;

        this.rideServiceClient =
                rideServiceClient;

        this.searchRadiusKm =
                searchRadiusKm;

        this.maxCandidates =
                maxCandidates;

        this.staleSeconds =
                staleSeconds;
    }


    @Override
    @Transactional
    public MatchResponse matchRide(
            MatchRideRequest request) {

        /*
         * Idempotency:
         *
         * If this ride has already been processed,
         * return the existing match instead of creating
         * another match.
         */

        MatchRecord existing =
                matchRecordRepository

                        .findByRideId(
                                request.getRideId()
                        )

                        .orElse(null);


        if (existing != null) {

            return toResponse(existing);
        }


        /*
         * Create SEARCHING record.
         */

        MatchRecord record =
                MatchRecord.builder()

                        .rideId(
                                request.getRideId()
                        )

                        .riderId(
                                request.getRiderId()
                        )

                        .status(
                                MatchStatus.SEARCHING
                        )

                        .createdAt(
                                Instant.now()
                        )

                        .updatedAt(
                                Instant.now()
                        )

                        .build();


        matchRecordRepository.save(record);


        /*
         * Get available drivers.
         *
         * This method will be connected to your
         * existing driver-service endpoint.
         */

        List<Long> availableDriverIds =
                getAvailableDriverIds();


        /*
         * Build driver candidates.
         */

        List<DriverCandidateResponse> candidates =

                availableDriverIds

                        .stream()

                        .limit(maxCandidates)

                        .map(driverId ->
                                buildCandidate(
                                        driverId,
                                        request
                                )
                        )

                        .filter(
                                candidate ->
                                        candidate != null
                        )

                        /*
                         * Ignore candidates without
                         * location timestamp.
                         */

                        .filter(
                                candidate ->
                                        candidate.getRecordedAt()
                                                != null
                        )

                        /*
                         * Ignore stale locations.
                         */

                        .filter(candidate ->

                                Duration.between(
                                        candidate.getRecordedAt(),
                                        Instant.now()
                                ).getSeconds()
                                        <= staleSeconds
                        )

                        /*
                         * Only drivers within radius.
                         */

                        .filter(candidate ->

                                candidate.getDistanceKm()
                                        <= searchRadiusKm
                        )

                        /*
                         * Nearest driver first.
                         */

                        .sorted(
                                Comparator.comparing(
                                        DriverCandidateResponse
                                                ::getDistanceKm
                                )
                        )

                        .toList();


        /*
         * No driver available.
         */

        if (candidates.isEmpty()) {

            record.setStatus(
                    MatchStatus.FAILED
            );

            record.setUpdatedAt(
                    Instant.now()
            );

            matchRecordRepository.save(record);


            return MatchResponse.builder()

                    .rideId(
                            request.getRideId()
                    )

                    .status(
                            "NO_DRIVER_FOUND"
                    )

                    .message(
                            "No available driver found " +
                            "within the configured search radius"
                    )

                    .build();
        }


        /*
         * Select nearest driver.
         */

        DriverCandidateResponse winner =
                candidates.get(0);


        /*
         * Assign driver in driver-service.
         */

        driverServiceClient.assignDriver(

                winner.getDriverId(),

                request.getRideId()
        );


        /*
         * Update ride-service.
         */

        rideServiceClient.assignDriver(

                request.getRideId(),

                winner.getDriverId()
        );


        /*
         * Persist successful match.
         */

        record.setDriverId(
                winner.getDriverId()
        );

        record.setDistanceKm(
                winner.getDistanceKm()
        );

        record.setStatus(
                MatchStatus.MATCHED
        );

        record.setUpdatedAt(
                Instant.now()
        );


        matchRecordRepository.save(record);


        return toResponse(record);
    }


    /*
     * Driver discovery.
     *
     * We will replace the body with the exact
     * endpoint from your driver-service.
     */

    private List<Long>
    getAvailableDriverIds() {

        throw new IllegalStateException(

                "Driver discovery endpoint is not configured yet"
        );
    }


    /*
     * Build one candidate.
     */

    private DriverCandidateResponse
    buildCandidate(

            Long driverId,

            MatchRideRequest request) {

        try {

            /*
             * Verify driver exists.
             */

            DriverResponse driver =
                    driverServiceClient.getDriver(
                            driverId
                    );


            if (driver == null ||
                    driver.getDriverId() == null) {

                return null;
            }


            /*
             * Get latest GPS location.
             */

            DriverLocationResponse location =
                    trackingServiceClient
                            .getLatestDriverLocation(
                                    driverId
                            );


            if (location == null ||

                    location.getLatitude() == null ||

                    location.getLongitude() == null) {

                return null;
            }


            /*
             * Calculate distance from rider
             * pickup point to driver.
             */

            double distanceKm =
                    haversineKm(

                            request.getPickupLatitude(),

                            request.getPickupLongitude(),

                            location.getLatitude(),

                            location.getLongitude()
                    );


            return DriverCandidateResponse.builder()

                    .driverId(driverId)

                    .latitude(
                            location.getLatitude()
                    )

                    .longitude(
                            location.getLongitude()
                    )

                    .recordedAt(
                            location.getRecordedAt()
                    )

                    .distanceKm(
                            distanceKm
                    )

                    .build();


        } catch (RuntimeException exception) {

            /*
             * One unavailable driver should not
             * break the complete matching request.
             */

            return null;
        }
    }


    /*
     * Haversine distance.
     *
     * Returns distance in kilometres.
     */

    private double haversineKm(

            double lat1,
            double lon1,

            double lat2,
            double lon2) {


        final double EARTH_RADIUS_KM =
                6371.0;


        double dLat =
                Math.toRadians(
                        lat2 - lat1
                );


        double dLon =
                Math.toRadians(
                        lon2 - lon1
                );


        double a =

                Math.sin(dLat / 2)
                        * Math.sin(dLat / 2)

                +

                Math.cos(
                        Math.toRadians(lat1)
                )

                *

                Math.cos(
                        Math.toRadians(lat2)
                )

                *

                Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);


        double c =

                2 *

                Math.atan2(

                        Math.sqrt(a),

                        Math.sqrt(1 - a)
                );


        return EARTH_RADIUS_KM * c;
    }


    @Override
    @Transactional(readOnly = true)
    public MatchResponse getMatch(
            UUID rideId) {

        MatchRecord record =

                matchRecordRepository

                        .findByRideId(rideId)

                        .orElseThrow(() ->

                                new MatchNotFoundException(

                                        "No match found for ride: "
                                                + rideId
                                )
                        );


        return toResponse(record);
    }


    @Override
    @Transactional
    public MatchResponse cancelMatch(
            UUID rideId) {

        MatchRecord record =

                matchRecordRepository

                        .findByRideId(rideId)

                        .orElseThrow(() ->

                                new MatchNotFoundException(

                                        "No match found for ride: "
                                                + rideId
                                )
                        );


        if (record.getStatus() ==
                MatchStatus.MATCHED) {

            throw new IllegalStateException(
                    "Matched ride cannot be cancelled " +
                    "by matching-service"
            );
        }


        record.setStatus(
                MatchStatus.CANCELLED
        );

        record.setUpdatedAt(
                Instant.now()
        );


        matchRecordRepository.save(record);


        return toResponse(record);
    }


    private MatchResponse toResponse(
            MatchRecord record) {

        return MatchResponse.builder()

                .rideId(
                        record.getRideId()
                )

                .driverId(
                        record.getDriverId()
                )

                .status(
                        record.getStatus().name()
                )

                .distanceKm(
                        record.getDistanceKm()
                )

                .message(

                        record.getStatus()
                                == MatchStatus.MATCHED

                                ?

                                "Driver matched successfully"

                                :

                                "Matching status: "
                                        + record.getStatus()
                )

                .build();
    }
}