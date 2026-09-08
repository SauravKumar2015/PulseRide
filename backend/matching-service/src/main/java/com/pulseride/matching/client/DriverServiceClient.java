package com.pulseride.matching.client;

import java.util.UUID;

import com.pulseride.matching.dto.response.DriverResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.springframework.web.client.RestClient;

@Component
public class DriverServiceClient {

    private final RestClient driverRestClient;


    public DriverServiceClient(
            @Qualifier("driverRestClient")
            RestClient driverRestClient) {

        this.driverRestClient =
                driverRestClient;
    }


    public DriverResponse getDriver(
            Long driverId) {

        return driverRestClient

                .get()

                .uri(
                        "/internal/drivers/{driverId}",
                        driverId
                )

                .retrieve()

                .body(
                        DriverResponse.class
                );
    }


    public void assignDriver(
            Long driverId,
            UUID rideId) {

        driverRestClient

                .patch()

                .uri(
                        "/internal/drivers/{driverId}/assign/{rideId}",
                        driverId,
                        rideId
                )

                .retrieve()

                .toBodilessEntity();
    }
}