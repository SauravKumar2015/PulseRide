package com.pulseride.ride.dto;

import com.pulseride.ride.entity.RideStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateRideStatusRequest {

    @NotNull(message = "Status is required")
    private RideStatus status;

    @Size(
            max = 500,
            message = "Reason must not exceed 500 characters"
    )
    private String reason;
}