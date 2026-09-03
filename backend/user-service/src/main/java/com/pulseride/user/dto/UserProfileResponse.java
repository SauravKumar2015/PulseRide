package com.pulseride.user.dto;

import java.time.LocalDateTime;

public record UserProfileResponse(Long id, String displayName, String email, String role, LocalDateTime updatedAt) {
}
