package com.pulseride.tracking.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pulseride.tracking.client.DriverServiceClient;
import com.pulseride.tracking.dto.request.DriverLocationRequest;
import com.pulseride.tracking.dto.response.DriverLocationResponse;
import com.pulseride.tracking.dto.response.DriverProfileResponse;
import com.pulseride.tracking.dto.response.LocationHistoryResponse;
import com.pulseride.tracking.dto.response.RideTrackingResponse;
import com.pulseride.tracking.entity.DriverLocation;
import com.pulseride.tracking.exception.LocationNotFoundException;
import com.pulseride.tracking.repository.DriverLocationRepository;

@Service
public class TrackingServiceImpl implements TrackingService {

    private final DriverLocationRepository driverLocationRepository;
    private final DriverServiceClient driverServiceClient;

    public TrackingServiceImpl(
            DriverLocationRepository driverLocationRepository,
            DriverServiceClient driverServiceClient) {

        this.driverLocationRepository = driverLocationRepository;
        this.driverServiceClient = driverServiceClient;
    }

    @Override
    @Transactional
    public DriverLocationResponse updateDriverLocation(
            Long driverId,
            DriverLocationRequest request) {

        DriverProfileResponse driverProfile =
                driverServiceClient.createDriverProfile(driverId);

        if (driverProfile == null) {
            throw new IllegalStateException(
                    "Driver profile could not be verified"
            );
        }

        DriverLocation location = DriverLocation.builder()
                .driverId(driverId)
                .rideId(request.getRideId())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .recordedAt(Instant.now())
                .build();

        DriverLocation saved =
                driverLocationRepository.save(location);

        return toDriverLocationResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DriverLocationResponse getLatestDriverLocation(
            Long driverId) {

        DriverLocation location =
                driverLocationRepository
                        .findTopByDriverIdOrderByRecordedAtDesc(driverId)
                        .orElseThrow(
                                () -> new LocationNotFoundException(
                                        "No location found for driver: "
                                                + driverId
                                )
                        );

        return toDriverLocationResponse(location);
    }

    @Override
    @Transactional(readOnly = true)
    public RideTrackingResponse getLatestRideLocation(
            UUID rideId) {

        DriverLocation location =
                driverLocationRepository
                        .findTopByRideIdOrderByRecordedAtDesc(rideId)
                        .orElseThrow(
                                () -> new LocationNotFoundException(
                                        "No location found for ride: "
                                                + rideId
                                )
                        );

        return RideTrackingResponse.builder()
                .rideId(location.getRideId())
                .driverId(location.getDriverId())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .recordedAt(location.getRecordedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationHistoryResponse> getRideLocationHistory(
            UUID rideId) {

        return driverLocationRepository
                .findByRideIdOrderByRecordedAtAsc(rideId)
                .stream()
                .map(location ->
                        LocationHistoryResponse.builder()
                                .latitude(location.getLatitude())
                                .longitude(location.getLongitude())
                                .recordedAt(location.getRecordedAt())
                                .build()
                )
                .toList();
    }

    private DriverLocationResponse toDriverLocationResponse(
            DriverLocation location) {

        return DriverLocationResponse.builder()
                .driverId(location.getDriverId())
                .rideId(location.getRideId())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .recordedAt(location.getRecordedAt())
                .build();
    }
}