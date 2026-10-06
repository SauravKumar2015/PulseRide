package com.pulseride.ride.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pulseride.ride.dto.AssignDriverRequest;
import com.pulseride.ride.dto.CancelRideRequest;
import com.pulseride.ride.dto.CreateRideRequest;
import com.pulseride.ride.dto.RideResponse;
import com.pulseride.ride.dto.RideStatusHistoryResponse;
import com.pulseride.ride.dto.UpdateRideStatusRequest;
import com.pulseride.ride.service.RideService;

import jakarta.validation.Valid;

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

        return ResponseEntity.ok(
                rideService.getRide(
                        rideId,
                        userId
                )
        );
    }

    @GetMapping("/history")
    public ResponseEntity<List<RideResponse>>
    getRideHistory(
            Authentication authentication) {

        Long riderId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                rideService.getRideHistory(
                        riderId
                )
        );
    }

    @GetMapping("/driver/history")
    public ResponseEntity<List<RideResponse>>
    getDriverRideHistory(
            Authentication authentication) {

        Long driverId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                rideService.getDriverRideHistory(
                        driverId
                )
        );
    }

    @PostMapping("/{rideId}/cancel")
    public ResponseEntity<RideResponse> cancelRide(
            @PathVariable UUID rideId,
            @Valid
            @RequestBody(required = false)
            CancelRideRequest request,
            Authentication authentication) {

        Long riderId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                rideService.cancelRide(
                        rideId,
                        riderId,
                        request
                )
        );
    }

    @GetMapping("/{rideId}/history")
    public ResponseEntity<
            List<RideStatusHistoryResponse>>
    getRideStatusHistory(
            @PathVariable UUID rideId,
            Authentication authentication) {

        Long userId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                rideService.getRideStatusHistory(
                        rideId,
                        userId
                )
        );
    }

    @PostMapping("/{rideId}/assign-driver")
    public ResponseEntity<RideResponse> assignDriver(
            @PathVariable UUID rideId,
            @Valid @RequestBody
            AssignDriverRequest request) {

        return ResponseEntity.ok(
                rideService.assignDriver(
                        rideId,
                        request.getDriverId()
                )
        );
    }

    @PutMapping("/{rideId}/status")
    public ResponseEntity<RideResponse>
    updateRideStatus(
            @PathVariable UUID rideId,
            @Valid @RequestBody
            UpdateRideStatusRequest request,
            Authentication authentication) {

        Long userId =
                getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                rideService.updateRideStatus(
                        rideId,
                        userId,
                        request
                )
        );
    }

    private Long getAuthenticatedUserId(
            Authentication authentication) {

        if (authentication == null
                || authentication.getName() == null) {

            throw new SecurityException(
                    "User is not authenticated"
            );
        }

        return Long.valueOf(
                authentication.getName()
        );
    }
}