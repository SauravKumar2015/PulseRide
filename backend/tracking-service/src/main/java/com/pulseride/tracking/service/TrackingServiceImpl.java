package com.pulseride.tracking.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pulseride.tracking.client.DriverServiceClient;
import com.pulseride.tracking.dto.request.DriverLocationRequest;
import com.pulseride.tracking.dto.response.DriverLocationResponse;
import com.pulseride.tracking.dto.response.DriverProfileResponse;
import com.pulseride.tracking.entity.DriverLocation;
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

        /*
         * Verify that the authenticated user has a driver profile.
         */
        DriverProfileResponse driverProfile =
                driverServiceClient.createDriverProfile(driverId);

        if (driverProfile == null) {
            throw new IllegalStateException(
                    "Driver profile could not be verified"
            );
        }

        /*
         * Create a new location record.
         */
        DriverLocation location = DriverLocation.builder()
                .driverId(driverId)
                .rideId(request.getRideId())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .recordedAt(Instant.now())
                .build();

        /*
         * Save location in PostgreSQL.
         */
        DriverLocation saved =
                driverLocationRepository.save(location);

        /*
         * Convert entity to response DTO.
         */
        return DriverLocationResponse.builder()
                .driverId(saved.getDriverId())
                .rideId(saved.getRideId())
                .latitude(saved.getLatitude())
                .longitude(saved.getLongitude())
                .recordedAt(saved.getRecordedAt())
                .build();
    }
}