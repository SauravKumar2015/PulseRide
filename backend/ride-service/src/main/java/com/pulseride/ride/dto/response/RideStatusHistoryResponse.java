package com.pulseride.ride.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pulseride.ride.entity.RideStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RideStatusHistoryResponse {

    private UUID historyId;

    private UUID rideId;

    private RideStatus status;

    private LocalDateTime changedAt;

    private UUID changedBy;

    private String reason;
}