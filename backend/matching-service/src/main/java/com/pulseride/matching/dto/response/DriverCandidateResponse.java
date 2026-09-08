package com.pulseride.matching.dto.response;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriverCandidateResponse {

    private Long driverId;

    private Double latitude;

    private Double longitude;

    private Instant recordedAt;

    private Double distanceKm;
}