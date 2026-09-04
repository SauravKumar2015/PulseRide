package com.pulseride.ride.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateRideRequest {

    @NotNull(message = "Pickup latitude is required")
    @DecimalMin(
            value = "-90.0",
            message = "Pickup latitude must be between -90 and 90"
    )
    @DecimalMax(
            value = "90.0",
            message = "Pickup latitude must be between -90 and 90"
    )
    private BigDecimal pickupLatitude;

    @NotNull(message = "Pickup longitude is required")
    @DecimalMin(
            value = "-180.0",
            message = "Pickup longitude must be between -180 and 180"
    )
    @DecimalMax(
            value = "180.0",
            message = "Pickup longitude must be between -180 and 180"
    )
    private BigDecimal pickupLongitude;

    @NotNull(message = "Dropoff latitude is required")
    @DecimalMin(
            value = "-90.0",
            message = "Dropoff latitude must be between -90 and 90"
    )
    @DecimalMax(
            value = "90.0",
            message = "Dropoff latitude must be between -90 and 90"
    )
    private BigDecimal dropoffLatitude;

    @NotNull(message = "Dropoff longitude is required")
    @DecimalMin(
            value = "-180.0",
            message = "Dropoff longitude must be between -180 and 180"
    )
    @DecimalMax(
            value = "180.0",
            message = "Dropoff longitude must be between -180 and 180"
    )
    private BigDecimal dropoffLongitude;
}