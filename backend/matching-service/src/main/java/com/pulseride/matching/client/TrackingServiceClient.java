package com.pulseride.matching.client;

import com.pulseride.matching.dto.response.DriverLocationResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.springframework.web.client.RestClient;

@Component
public class TrackingServiceClient {

    private final RestClient trackingRestClient;


    public TrackingServiceClient(
            @Qualifier("trackingRestClient")
            RestClient trackingRestClient) {

        this.trackingRestClient =
                trackingRestClient;
    }


    public DriverLocationResponse
    getLatestDriverLocation(Long driverId) {

        return trackingRestClient

                .get()

                .uri(
                        "/tracking/driver/{driverId}/latest",
                        driverId
                )

                .retrieve()

                .body(
                        DriverLocationResponse.class
                );
    }
}