package com.pulseride.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class DriverClient {

    private final RestClient restClient;
    private final String driverServiceUrl;

    public DriverClient(
            RestClient restClient,
            @Value("${services.driver.url}") String driverServiceUrl) {

        this.restClient = restClient;
        this.driverServiceUrl = driverServiceUrl;
    }

    public void createDriverProfile(Long userId) {

        System.out.println(
                "Calling driver-service to create profile for userId: "
                        + userId
        );

        restClient.post()
                .uri(
                        driverServiceUrl
                                + "/pulse-ride/internal/drivers/profile"
                )
                .contentType(MediaType.TEXT_PLAIN)
                .body(userId.toString())
                .retrieve()
                .toBodilessEntity();

        System.out.println(
                "Driver profile created for userId: "
                        + userId
        );
    }
}