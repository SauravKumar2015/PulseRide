package com.pulseride.ride.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pulseride.ride.entity.RideStatusHistory;

public interface RideStatusHistoryRepository
        extends JpaRepository<RideStatusHistory, UUID> {

    List<RideStatusHistory> findByRideIdOrderByChangedAtAsc(UUID rideId);
}