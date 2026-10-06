package com.pulseride.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UserClient {

    private final RestClient restClient;
    private final String userServiceUrl;

    public UserClient(
            RestClient restClient,
            @Value("${services.user.url}") String userServiceUrl) {

        this.restClient = restClient;
        this.userServiceUrl = userServiceUrl;
    }

    public void createUserProfile(
            Long userId,
            String name,
            String email,
            String role) {

        System.out.println(
                "Calling user-service to create profile for userId: "
                        + userId
        );

        restClient.post()
                .uri(
                        userServiceUrl
                                + "/pulse-ride/internal/users/profile"
                )
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        new UserProfileRequest(
                                userId,
                                name,
                                email,
                                role
                        )
                )
                .retrieve()
                .toBodilessEntity();

        System.out.println(
                "User profile created for userId: "
                        + userId
        );
    }

    private record UserProfileRequest(
            Long id,
            String name,
            String email,
            String role) {
    }
}