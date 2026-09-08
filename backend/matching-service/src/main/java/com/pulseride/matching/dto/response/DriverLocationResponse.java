package com.pulseride.matching.dto.response;

import java.time.Instant;

import lombok.Data;

@Data
public class DriverLocationResponse {

    private Long driverId;

    private Object rideId;

    private Double latitude;

    private Double longitude;

    private Instant recordedAt;
}