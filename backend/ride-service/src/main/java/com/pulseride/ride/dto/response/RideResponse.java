package com.pulseride.ride.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.pulseride.ride.entity.RideStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RideResponse {

    private UUID rideId;

    private Long riderId;

    private Long driverId;

    private RideStatus status;

    private BigDecimal pickupLatitude;

    private BigDecimal pickupLongitude;

    private BigDecimal dropoffLatitude;

    private BigDecimal dropoffLongitude;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}