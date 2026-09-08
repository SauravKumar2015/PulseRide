package com.pulseride.tracking.controller;

import com.pulseride.tracking.dto.request.DriverLocationRequest;
import com.pulseride.tracking.dto.response.DriverLocationResponse;
import com.pulseride.tracking.service.TrackingService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tracking")
public class TrackingController {

    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @PostMapping("/location")
    public DriverLocationResponse updateDriverLocation(
            Authentication authentication,
            @Valid @RequestBody DriverLocationRequest request) {

        Long driverId =
                Long.valueOf(authentication.getName());

        return trackingService.updateDriverLocation(
                driverId,
                request
        );
    }
}