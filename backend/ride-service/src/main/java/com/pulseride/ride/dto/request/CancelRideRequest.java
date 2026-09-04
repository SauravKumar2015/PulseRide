package com.pulseride.ride.dto.request;

import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CancelRideRequest {

    @Size(
            max = 500,
            message = "Cancellation reason cannot exceed 500 characters"
    )
    private String reason;
}