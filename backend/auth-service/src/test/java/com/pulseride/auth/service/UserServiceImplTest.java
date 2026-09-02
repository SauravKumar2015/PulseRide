package com.pulseride.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pulseride.auth.dto.LoginRequest;
import com.pulseride.auth.dto.LogoutRequest;
import com.pulseride.auth.dto.RefreshTokenRequest;
import com.pulseride.auth.dto.RegisterRequest;
import com.pulseride.auth.dto.TokenResponse;
import com.pulseride.auth.entity.RefreshToken;
import com.pulseride.auth.entity.User;
import com.pulseride.auth.repository.RefreshTokenRepository;
import com.pulseride.auth.repository.UserRepository;
import com.pulseride.auth.security.JwtService;
import com.pulseride.auth.security.RefreshTokenService;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock UserRepository userRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock JwtService jwtService;
    @Mock RefreshTokenService refreshTokenService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private UserServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserServiceImpl(userRepository, refreshTokenRepository, passwordEncoder, jwtService, refreshTokenService);
    }

    @Test
    void registerHashesPasswordAndDoesNotReturnIt() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Sam Rider");
        request.setEmail("SAM@example.com");
        request.setPassword("Password1!");
        User saved = User.builder().id(1L).name("Sam Rider").email("sam@example.com")
            .password("encoded").role("PASSENGER").createdAt(LocalDateTime.now()).build();
        when(userRepository.existsByEmail("sam@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        var response = service.register(request);

        assertThat(response.getEmail()).isEqualTo("sam@example.com");
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(passwordEncoder.matches("Password1!", userCaptor.getValue().getPassword())).isTrue();
    }

    @Test
    void loginUsesGenericFailureForUnknownUser() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(new LoginRequest("unknown@example.com", "Password1!")))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Authentication failed");
    }

    @Test
    void refreshRevokesOldTokenAndIssuesReplacement() {
        User user = User.builder().id(1L).role("PASSENGER").build();
        RefreshToken stored = new RefreshToken();
        stored.setUser(user);
        stored.setExpiresAt(LocalDateTime.now().plusDays(1));
        when(refreshTokenService.findValid("old-token")).thenReturn(stored);
        when(jwtService.createAccessToken(user)).thenReturn("access");
        when(jwtService.getAccessTokenExpirationSeconds()).thenReturn(900L);
        when(refreshTokenService.create(user)).thenReturn("new-token");

        TokenResponse response = service.refresh(new RefreshTokenRequest("old-token"));

        assertThat(stored.isRevoked()).isTrue();
        assertThat(response.refreshToken()).isEqualTo("new-token");
        verify(refreshTokenRepository).save(stored);
    }

    @Test
    void logoutRevokesOnlyTokenOwnedByAuthenticatedUser() {
        User user = User.builder().id(7L).build();
        RefreshToken stored = new RefreshToken();
        stored.setUser(user);
        when(refreshTokenService.findValid("refresh-token")).thenReturn(stored);

        service.logout(new LogoutRequest("refresh-token"), "7");

        assertThat(stored.isRevoked()).isTrue();
    }
}
