package com.pulseride.driver.dto;

import jakarta.validation.constraints.NotNull;

public record VehicleTypeRequest(@NotNull String vehicleType) {
}
