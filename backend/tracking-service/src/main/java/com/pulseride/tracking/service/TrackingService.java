package com.pulseride.tracking.service;

import java.util.List;
import java.util.UUID;

import com.pulseride.tracking.dto.request.DriverLocationRequest;
import com.pulseride.tracking.dto.response.DriverLocationResponse;
import com.pulseride.tracking.dto.response.LocationHistoryResponse;
import com.pulseride.tracking.dto.response.RideTrackingResponse;

public interface TrackingService {

    DriverLocationResponse updateDriverLocation(
            Long driverId,
            DriverLocationRequest request
    );

    DriverLocationResponse getLatestDriverLocation(
            Long driverId
    );

    RideTrackingResponse getLatestRideLocation(
            UUID rideId
    );

    List<LocationHistoryResponse> getRideLocationHistory(
            UUID rideId
    );
}