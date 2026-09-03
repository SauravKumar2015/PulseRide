package com.pulseride.driver.dto;

import com.pulseride.driver.service.DriverStatus;

import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull DriverStatus status) {
}
