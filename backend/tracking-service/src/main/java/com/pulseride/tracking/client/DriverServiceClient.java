package com.pulseride.tracking.client;

import com.pulseride.tracking.dto.response.DriverProfileResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class DriverServiceClient {

    private final RestClient driverRestClient;

    public DriverServiceClient(
            @Qualifier("driverRestClient") RestClient driverRestClient) {

        this.driverRestClient = driverRestClient;
    }

    public DriverProfileResponse createDriverProfile(Long userId) {

        return driverRestClient
                .post()
                .uri("/pulse-ride/internal/drivers/profile")
                .body(userId.toString())
                .retrieve()
                .body(DriverProfileResponse.class);
    }
}