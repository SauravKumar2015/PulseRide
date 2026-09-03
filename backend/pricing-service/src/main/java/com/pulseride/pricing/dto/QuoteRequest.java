package com.pulseride.pricing.dto;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;
public record QuoteRequest(@NotNull @PositiveOrZero BigDecimal distanceKm, @NotNull @PositiveOrZero BigDecimal durationMinutes) {}
