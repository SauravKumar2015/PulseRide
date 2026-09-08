package com.pulseride.matching.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean("driverRestClient")
    public RestClient driverRestClient(
            @Value("${services.driver.url}") String url) {

        return RestClient.builder()
                .baseUrl(url)
                .build();
    }

    @Bean("trackingRestClient")
    public RestClient trackingRestClient(
            @Value("${services.tracking.url}") String url) {

        return RestClient.builder()
                .baseUrl(url)
                .build();
    }

    @Bean("rideRestClient")
    public RestClient rideRestClient(
            @Value("${services.ride.url}") String url) {

        return RestClient.builder()
                .baseUrl(url)
                .build();
    }
}

