package com.pulseride.tracking.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

import com.pulseride.tracking.client.DriverServiceClient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverProfileResponse {

    private String driverId;

    private String userId;

    private String status;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Instant lastLocationUpdate;

    private Instant createdAt;

    private Instant updatedAt;

    private String vehicleType;
}