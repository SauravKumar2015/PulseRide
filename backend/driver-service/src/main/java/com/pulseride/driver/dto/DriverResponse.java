package com.pulseride.driver.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.pulseride.driver.service.DriverStatus;

public record DriverResponse(
	String driverId,
	String userId,
	DriverStatus status,
	BigDecimal latitude,
	BigDecimal longitude,
	Instant lastLocationUpdate,
	Instant createdAt,
	Instant updatedAt) {
}
