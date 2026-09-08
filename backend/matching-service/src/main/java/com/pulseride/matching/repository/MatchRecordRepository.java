package com.pulseride.matching.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pulseride.matching.entity.MatchRecord;

public interface MatchRecordRepository
        extends JpaRepository<MatchRecord, Long> {

    Optional<MatchRecord> findByRideId(UUID rideId);

    boolean existsByRideId(UUID rideId);
}