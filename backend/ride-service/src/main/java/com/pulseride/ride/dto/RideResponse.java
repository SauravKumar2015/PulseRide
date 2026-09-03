package com.pulseride.ride.dto;
import java.time.Instant;
import com.pulseride.ride.service.RideStatus;
public record RideResponse(String id, String passengerId, Double pickupLatitude, Double pickupLongitude,
                           Double dropoffLatitude, Double dropoffLongitude, RideStatus status, Instant createdAt) {}
