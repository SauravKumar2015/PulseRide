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

import jakarta.validation.Valid;

import com.pulseride.ride.dto.CancelRideRequest;
import com.pulseride.ride.dto.CreateRideRequest;
import com.pulseride.ride.dto.RideResponse;
import com.pulseride.ride.dto.RideStatusHistoryResponse;
import com.pulseride.ride.service.RideService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @PostMapping
    public ResponseEntity<RideResponse> createRide(
            @Valid @RequestBody CreateRideRequest request,
            Authentication authentication) {

        Long riderId =
                getAuthenticatedUserId(authentication);

        RideResponse response =
                rideService.createRide(
                        riderId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponse> getRide(
            @PathVariable UUID rideId,
            Authentication authentication) {

        Long userId =
                getAuthenticatedUserId(authentication);

        RideResponse response =
                rideService.getRide(
                        rideId,
                        userId
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<RideResponse>> getRideHistory(
            Authentication authentication) {

        Long riderId =
                getAuthenticatedUserId(authentication);

        List<RideResponse> rides =
                rideService.getRideHistory(riderId);

        return ResponseEntity.ok(rides);
    }

    @PostMapping("/{rideId}/cancel")
    public ResponseEntity<RideResponse> cancelRide(
            @PathVariable UUID rideId,
            @Valid @RequestBody(required = false)
            CancelRideRequest request,
            Authentication authentication) {

        Long riderId =
                getAuthenticatedUserId(authentication);

        RideResponse response =
                rideService.cancelRide(
                        rideId,
                        riderId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{rideId}/history")
    public ResponseEntity<List<RideStatusHistoryResponse>>
    getRideStatusHistory(
            @PathVariable UUID rideId,
            Authentication authentication) {

        Long userId =
                getAuthenticatedUserId(authentication);

        List<RideStatusHistoryResponse> history =
                rideService.getRideStatusHistory(
                        rideId,
                        userId
                );

        return ResponseEntity.ok(history);
    }

    private Long getAuthenticatedUserId(
            Authentication authentication) {

        return Long.valueOf(authentication.getName());
    }
}