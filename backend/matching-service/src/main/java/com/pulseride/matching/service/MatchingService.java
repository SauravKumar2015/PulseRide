package com.pulseride.matching.service;

import java.util.UUID;

import com.pulseride.matching.dto.request.MatchRideRequest;
import com.pulseride.matching.dto.response.MatchResponse;

public interface MatchingService {

    MatchResponse matchRide(
            MatchRideRequest request
    );


    MatchResponse getMatch(
            UUID rideId
    );


    MatchResponse cancelMatch(
            UUID rideId
    );
}