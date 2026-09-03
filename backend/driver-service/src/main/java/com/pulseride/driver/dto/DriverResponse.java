package com.pulseride.driver.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.pulseride.driver.service.DriverStatus;
import com.pulseride.driver.service.VehicleType;

public record DriverResponse(
    String driverId,
    String userId,
    DriverStatus status,
    BigDecimal latitude,
    BigDecimal longitude,
    Instant lastLocationUpdate,
    Instant createdAt,
    Instant updatedAt,
    VehicleType vehicleType) {
}