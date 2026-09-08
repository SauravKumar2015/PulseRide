package com.pulseride.tracking.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideTrackingResponse {

    private UUID rideId;

    private Long driverId;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Instant recordedAt;
}