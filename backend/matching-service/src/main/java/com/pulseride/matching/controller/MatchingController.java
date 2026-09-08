package com.pulseride.matching.controller;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import com.pulseride.matching.dto.request.MatchRideRequest;
import com.pulseride.matching.dto.response.MatchResponse;
import com.pulseride.matching.service.MatchingService;

@RestController
@RequestMapping("/matching")
public class MatchingController {

    private final MatchingService matchingService;


    public MatchingController(
            MatchingService matchingService) {

        this.matchingService =
                matchingService;
    }


    @PostMapping("/rides")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public MatchResponse matchRide(

            @Valid
            @RequestBody
            MatchRideRequest request) {

        return matchingService.matchRide(
                request
        );
    }


    @GetMapping("/rides/{rideId}")
    @PreAuthorize(
            "hasAnyRole('USER', 'DRIVER', 'ADMIN')"
    )
    public MatchResponse getMatch(

            @PathVariable
            UUID rideId) {

        return matchingService.getMatch(
                rideId
        );
    }


    @DeleteMapping("/rides/{rideId}")
    @PreAuthorize(
            "hasAnyRole('USER', 'ADMIN')"
    )
    public MatchResponse cancelMatch(

            @PathVariable
            UUID rideId) {

        return matchingService.cancelMatch(
                rideId
        );
    }
}