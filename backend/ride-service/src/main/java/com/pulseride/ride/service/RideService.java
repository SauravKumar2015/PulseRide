package com.pulseride.ride.service;

import java.util.List;
import java.util.UUID;

import com.pulseride.ride.dto.AssignDriverRequest;
import com.pulseride.ride.dto.CancelRideRequest;
import com.pulseride.ride.dto.CreateRideRequest;
import com.pulseride.ride.dto.RideResponse;
import com.pulseride.ride.dto.RideStatusHistoryResponse;
import com.pulseride.ride.dto.UpdateRideStatusRequest;

public interface RideService {

    RideResponse createRide(
            Long riderId,
            CreateRideRequest request
    );

    RideResponse getRide(
            UUID rideId,
            Long userId
    );

    List<RideResponse> getRideHistory(
            Long riderId
    );

    RideResponse cancelRide(
            UUID rideId,
            Long riderId,
            CancelRideRequest request
    );

    List<RideStatusHistoryResponse> getRideStatusHistory(
            UUID rideId,
            Long userId
    );

    RideResponse assignDriver(
            UUID rideId,
            Long driverId
    );

    RideResponse updateRideStatus(
            UUID rideId,
            Long userId,
            UpdateRideStatusRequest request
    );

    List<RideResponse> getDriverRideHistory(
            Long driverId
    );
}