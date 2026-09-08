package com.pulseride.tracking.service;

import com.pulseride.tracking.dto.request.DriverLocationRequest;
import com.pulseride.tracking.dto.response.DriverLocationResponse;

public interface TrackingService {

    DriverLocationResponse updateDriverLocation(
            Long driverId,
            DriverLocationRequest request
    );
}