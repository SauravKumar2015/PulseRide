package com.pulseride.auth.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class UserResponse {
    

    private Long id;
    private String name;
    private String email;
    private String role;
    private LocalDateTime createdAt;
}
