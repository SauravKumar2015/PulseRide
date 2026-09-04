package com.pulseride.ride.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pulseride.ride.entity.Ride;
import com.pulseride.ride.entity.RideStatus;

public interface RideRepository extends JpaRepository<Ride, UUID> {

    List<Ride> findByRiderIdOrderByCreatedAtDesc(UUID riderId);

    Optional<Ride> findByRideIdAndRiderId(
            UUID rideId,
            UUID riderId
    );

    List<Ride> findByDriverIdOrderByCreatedAtDesc(UUID driverId);

    Optional<Ride> findByRideIdAndDriverId(
            UUID rideId,
            UUID driverId
    );

    List<Ride> findByStatus(RideStatus status);
}