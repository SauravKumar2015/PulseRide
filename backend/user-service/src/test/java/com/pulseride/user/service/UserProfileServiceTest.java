package com.pulseride.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import com.pulseride.user.repository.UserProfileRepository;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {
    @Mock UserProfileRepository repository;
    @InjectMocks UserProfileService service;

    @Test
    void provisionsProfileFromAuthClaims() {
        Jwt jwt = Jwt.withTokenValue("test").header("alg", "HS256").subject("42").claim("email", "rider@example.com")
                .claim("role", "PASSENGER").issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(60)).build();
        when(repository.findById(42L)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(service.getProfile(jwt).id()).isEqualTo(42L);
        assertThat(service.getProfile(jwt).role()).isEqualTo("PASSENGER");
    }
}
