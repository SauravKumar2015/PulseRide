package com.pulseride.ride.service;

import java.util.List;
import java.util.UUID;

import com.pulseride.ride.dto.request.CancelRideRequest;
import com.pulseride.ride.dto.request.CreateRideRequest;
import com.pulseride.ride.dto.response.RideResponse;
import com.pulseride.ride.dto.response.RideStatusHistoryResponse;

public interface RideService {

    RideResponse createRide(
            UUID riderId,
            CreateRideRequest request
    );

    RideResponse getRide(
            UUID rideId,
            UUID userId
    );

    List<RideResponse> getRideHistory(
            UUID riderId
    );

    RideResponse cancelRide(
            UUID rideId,
            UUID riderId,
            CancelRideRequest request
    );

    List<RideStatusHistoryResponse> getRideStatusHistory(
            UUID rideId,
            UUID userId
    );
}