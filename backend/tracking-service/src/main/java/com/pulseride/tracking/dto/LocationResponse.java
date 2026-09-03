package com.pulseride.tracking.dto;
import java.time.Instant;
public record LocationResponse(String driverId, Double latitude, Double longitude, Instant timestamp) {}
