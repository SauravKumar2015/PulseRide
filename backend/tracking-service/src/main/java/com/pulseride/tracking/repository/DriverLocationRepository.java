package com.pulseride.tracking.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pulseride.tracking.entity.DriverLocation;

public interface DriverLocationRepository
        extends JpaRepository<DriverLocation, Long> {

    Optional<DriverLocation> findTopByDriverIdOrderByRecordedAtDesc(
            Long driverId
    );

    Optional<DriverLocation> findTopByRideIdOrderByRecordedAtDesc(
            UUID rideId
    );

    List<DriverLocation> findByRideIdOrderByRecordedAtAsc(
            UUID rideId
    );

    List<DriverLocation> findByDriverIdOrderByRecordedAtDesc(
            Long driverId
    );
}