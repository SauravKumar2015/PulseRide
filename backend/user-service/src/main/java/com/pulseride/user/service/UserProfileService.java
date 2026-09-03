package com.pulseride.user.service;

import java.time.LocalDateTime;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pulseride.user.dto.UpdateProfileRequest;
import com.pulseride.user.dto.UserProfileResponse;
import com.pulseride.user.entity.UserProfile;
import com.pulseride.user.repository.UserProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository repository;

    @Transactional
    public UserProfileResponse getProfile(Jwt jwt) {
        UserProfile profile = repository.findById(userId(jwt)).orElseGet(() -> provision(jwt));
        return toResponse(profile);
    }

    @Transactional
    public UserProfileResponse updateProfile(Jwt jwt, UpdateProfileRequest request) {
        UserProfile profile = repository.findById(userId(jwt)).orElseGet(() -> provision(jwt));
        if (request.displayName() != null) {
            profile.setDisplayName(request.displayName().trim());
        }
        profile.setUpdatedAt(LocalDateTime.now());
        return toResponse(repository.save(profile));
    }

    private UserProfile provision(Jwt jwt) {
        UserProfile profile = new UserProfile(userId(jwt), jwt.getClaimAsString("email"),
                jwt.getClaimAsString("email"), jwt.getClaimAsString("role"));
        return repository.save(profile);
    }

    private Long userId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }

    private UserProfileResponse toResponse(UserProfile profile) {
        return new UserProfileResponse(profile.getId(), profile.getDisplayName(), profile.getEmail(),
                profile.getRole(), profile.getUpdatedAt());
    }
}
