package com.pulseride.ride.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pulseride.ride.entity.Ride;
import com.pulseride.ride.entity.RideStatus;

public interface RideRepository extends JpaRepository<Ride, UUID> {

    List<Ride> findByRiderIdOrderByCreatedAtDesc(Long riderId);

    List<Ride> findByDriverIdOrderByCreatedAtDesc(Long driverId);

    List<Ride> findByRiderIdAndStatus(
            Long riderId,
            RideStatus status
    );

    List<Ride> findByDriverIdAndStatus(
            Long driverId,
            RideStatus status
    );
}