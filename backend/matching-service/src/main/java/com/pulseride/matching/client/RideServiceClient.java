package com.pulseride.matching.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.springframework.web.client.RestClient;

@Component
public class RideServiceClient {

    private final RestClient rideRestClient;


    public RideServiceClient(
            @Qualifier("rideRestClient")
            RestClient rideRestClient) {

        this.rideRestClient =
                rideRestClient;
    }


    public void assignDriver(
            UUID rideId,
            Long driverId) {

        rideRestClient

                .patch()

                .uri(
                        "/rides/internal/{rideId}/driver/{driverId}",
                        rideId,
                        driverId
                )

                .retrieve()

                .toBodilessEntity();
    }
}