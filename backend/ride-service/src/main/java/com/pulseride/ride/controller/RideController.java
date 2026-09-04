package com.pulseride.ride.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pulseride.ride.dto.request.CancelRideRequest;
import com.pulseride.ride.dto.request.CreateRideRequest;
import com.pulseride.ride.dto.response.RideResponse;
import com.pulseride.ride.dto.response.RideStatusHistoryResponse;
import com.pulseride.ride.service.RideService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    /**
     * Create a new ride request.
     *
     * Authorization:
     * USER role
     */
    @PostMapping
    public ResponseEntity<RideResponse> createRide(
            @Valid @RequestBody CreateRideRequest request,
            Authentication authentication) {

        UUID riderId = getAuthenticatedUserId(authentication);

        RideResponse response =
                rideService.createRide(riderId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Get a specific ride.
     *
     * The authenticated user must be either:
     * - the rider who created the ride
     * - the driver assigned to the ride
     */
    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponse> getRide(
            @PathVariable UUID rideId,
            Authentication authentication) {

        UUID userId = getAuthenticatedUserId(authentication);

        RideResponse response =
                rideService.getRide(rideId, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * Get ride history of the authenticated rider.
     *
     * Authorization:
     * USER role
     */
    @GetMapping("/history")
    public ResponseEntity<List<RideResponse>> getRideHistory(
            Authentication authentication) {

        UUID riderId = getAuthenticatedUserId(authentication);

        List<RideResponse> rides =
                rideService.getRideHistory(riderId);

        return ResponseEntity.ok(rides);
    }

    /**
     * Cancel an existing ride.
     *
     * The authenticated user must own the ride.
     */
    @PostMapping("/{rideId}/cancel")
    public ResponseEntity<RideResponse> cancelRide(
            @PathVariable UUID rideId,
            @Valid @RequestBody(required = false)
            CancelRideRequest request,
            Authentication authentication) {

        UUID riderId = getAuthenticatedUserId(authentication);

        RideResponse response =
                rideService.cancelRide(
                        rideId,
                        riderId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Get complete status history of a ride.
     *
     * The authenticated user must have access to the ride.
     */
    @GetMapping("/{rideId}/history")
    public ResponseEntity<List<RideStatusHistoryResponse>>
            getRideStatusHistory(
                    @PathVariable UUID rideId,
                    Authentication authentication) {

        UUID userId = getAuthenticatedUserId(authentication);

        List<RideStatusHistoryResponse> history =
                rideService.getRideStatusHistory(
                        rideId,
                        userId
                );

        return ResponseEntity.ok(history);
    }

    /**
     * Extract the authenticated user's UUID from JWT subject.
     */
    private UUID getAuthenticatedUserId(
            Authentication authentication) {

        return UUID.fromString(authentication.getName());
    }
}