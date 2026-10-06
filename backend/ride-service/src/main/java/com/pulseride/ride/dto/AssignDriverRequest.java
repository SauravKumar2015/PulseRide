package com.pulseride.ride.dto;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AssignDriverRequest {

    @NotNull(message = "Driver ID is required")
    private Long driverId;
}