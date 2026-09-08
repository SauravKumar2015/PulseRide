package com.pulseride.tracking.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.pulseride.tracking.dto.request.DriverLocationRequest;
import com.pulseride.tracking.dto.response.DriverLocationResponse;
import com.pulseride.tracking.dto.response.LocationHistoryResponse;
import com.pulseride.tracking.dto.response.RideTrackingResponse;
import com.pulseride.tracking.service.TrackingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tracking")
public class TrackingController {

    private final TrackingService trackingService;

    public TrackingController(
            TrackingService trackingService) {

        this.trackingService = trackingService;
    }

    @PostMapping("/location")
    @PreAuthorize("hasRole('DRIVER')")
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

    @GetMapping("/driver/{driverId}/latest")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
    public DriverLocationResponse getLatestDriverLocation(
            @PathVariable Long driverId) {

        return trackingService.getLatestDriverLocation(
                driverId
        );
    }

    @GetMapping("/ride/{rideId}")
    @PreAuthorize("hasAnyRole('USER', 'DRIVER', 'ADMIN')")
    public RideTrackingResponse getLatestRideLocation(
            @PathVariable UUID rideId) {

        return trackingService.getLatestRideLocation(
                rideId
        );
    }

    @GetMapping("/ride/{rideId}/history")
    @PreAuthorize("hasAnyRole('USER', 'DRIVER', 'ADMIN')")
    public List<LocationHistoryResponse> getRideLocationHistory(
            @PathVariable UUID rideId) {

        return trackingService.getRideLocationHistory(
                rideId
        );
    }
}