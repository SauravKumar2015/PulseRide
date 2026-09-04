package com.pulseride.ride.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pulseride.ride.entity.RideStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RideStatusHistoryResponse {

    private UUID historyId;

    private UUID rideId;

    private Long changedBy;

    private RideStatus status;

    private String reason;

    private LocalDateTime changedAt;
}