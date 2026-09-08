package com.pulseride.matching.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchResponse {

    private UUID rideId;

    private Long driverId;

    private String status;

    private Double distanceKm;

    private String message;
}