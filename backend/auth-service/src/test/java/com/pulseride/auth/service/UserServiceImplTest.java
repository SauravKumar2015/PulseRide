package com.pulseride.auth.service;

import com.pulseride.auth.dto.LoginRequest;
import com.pulseride.auth.dto.LogoutRequest;
import com.pulseride.auth.dto.RefreshTokenRequest;
import com.pulseride.auth.dto.RegisterRequest;
import com.pulseride.auth.dto.TokenResponse;
import com.pulseride.auth.dto.UserResponse;
import com.pulseride.auth.entity.RefreshToken;
import com.pulseride.auth.entity.User;
import com.pulseride.auth.repository.RefreshTokenRepository;
import com.pulseride.auth.repository.UserRepository;
import com.pulseride.auth.security.JwtService;
import com.pulseride.auth.security.RefreshTokenService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private DriverClient driverClient;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(1L)
                .name("Saurav Kumar")
                .email("saurav@example.com")
                .password("encoded-password")
                .role("USER")
                .build();
    }

    @Test
    void register_shouldCreateUserSuccessfully() {

        RegisterRequest request = new RegisterRequest();

        request.setName("Saurav Kumar");
        request.setEmail("saurav@example.com");
        request.setPassword("Password@123");
        request.setRole("USER");

        when(userRepository.existsByEmail("saurav@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("Password@123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserResponse response =
                userService.register(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Saurav Kumar", response.getName());
        assertEquals("saurav@example.com", response.getEmail());
        assertEquals("USER", response.getRole());

        verify(userRepository)
                .save(any(User.class));

        verify(userClient)
                .createUserProfile(
                        1L,
                        "Saurav Kumar",
                        "saurav@example.com",
                        "USER"
                );

        verify(driverClient, never())
                .createDriverProfile(anyLong());
    }

    @Test
    void register_shouldCreateDriverAndUserProfilesForDriver() {

        RegisterRequest request = new RegisterRequest();

        request.setName("Gupta");
        request.setEmail("guptastores123@gmail.com");
        request.setPassword("Gupta@2000");
        request.setRole("DRIVER");

        User driver = User.builder()
                .id(2L)
                .name("Gupta")
                .email("guptastores123@gmail.com")
                .password("encoded-password")
                .role("DRIVER")
                .build();

        when(userRepository.existsByEmail(
                "guptastores123@gmail.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("Gupta@2000"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenReturn(driver);

        UserResponse response =
                userService.register(request);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("Gupta", response.getName());
        assertEquals(
                "guptastores123@gmail.com",
                response.getEmail()
        );
        assertEquals("DRIVER", response.getRole());

        verify(userClient)
                .createUserProfile(
                        2L,
                        "Gupta",
                        "guptastores123@gmail.com",
                        "DRIVER"
                );

        verify(driverClient)
                .createDriverProfile(2L);
    }

    @Test
    void register_shouldFailWhenEmailAlreadyExists() {

        RegisterRequest request = new RegisterRequest();

        request.setName("Saurav Kumar");
        request.setEmail("saurav@example.com");
        request.setPassword("Password@123");
        request.setRole("USER");

        when(userRepository.existsByEmail("saurav@example.com"))
                .thenReturn(true);

        assertThrows(
                RuntimeException.class,
                () -> userService.register(request)
        );

        verify(userRepository, never())
                .save(any(User.class));

        verify(userClient, never())
                .createUserProfile(
                        anyLong(),
                        anyString(),
                        anyString(),
                        anyString()
                );

        verify(driverClient, never())
                .createDriverProfile(anyLong());
    }

    @Test
    void login_shouldReturnTokensSuccessfully() {

        LoginRequest request =
                new LoginRequest(
                        "saurav@example.com",
                        "Password@123"
                );

        when(userRepository.findByEmail(
                "saurav@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "Password@123",
                "encoded-password"))
                .thenReturn(true);

        when(jwtService.createAccessToken(user))
                .thenReturn("access-token");

        when(refreshTokenService.create(user))
                .thenReturn("refresh-token");

        when(jwtService.getAccessTokenExpirationSeconds())
                .thenReturn(3600L);

        TokenResponse response =
                userService.login(request);

        assertNotNull(response);

        assertEquals(
                "access-token",
                response.accessToken()
        );

        assertEquals(
                "refresh-token",
                response.refreshToken()
        );

        assertEquals(
                "Bearer",
                response.tokenType()
        );
    }

    @Test
    void login_shouldFailWithWrongPassword() {

        LoginRequest request =
                new LoginRequest(
                        "saurav@example.com",
                        "WrongPassword"
                );

        when(userRepository.findByEmail(
                "saurav@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "WrongPassword",
                "encoded-password"))
                .thenReturn(false);

        assertThrows(
                RuntimeException.class,
                () -> userService.login(request)
        );

        verify(jwtService, never())
                .createAccessToken(any(User.class));
    }

    @Test
    void login_shouldFailWhenUserDoesNotExist() {

        LoginRequest request =
                new LoginRequest(
                        "unknown@example.com",
                        "Password@123"
                );

        when(userRepository.findByEmail(
                "unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> userService.login(request)
        );
    }

    @Test
    void refresh_shouldReturnNewTokens() {

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setRevoked(false);

        when(refreshTokenService.findValid(
                "old-refresh-token"))
                .thenReturn(refreshToken);

        when(jwtService.createAccessToken(user))
                .thenReturn("new-access-token");

        when(refreshTokenService.create(user))
                .thenReturn("new-refresh-token");

        when(jwtService.getAccessTokenExpirationSeconds())
                .thenReturn(3600L);

        RefreshTokenRequest request =
                new RefreshTokenRequest(
                        "old-refresh-token"
                );

        TokenResponse response =
                userService.refresh(request);

        assertNotNull(response);

        assertEquals(
                "new-access-token",
                response.accessToken()
        );

        assertEquals(
                "new-refresh-token",
                response.refreshToken()
        );

        assertTrue(refreshToken.isRevoked());

        verify(refreshTokenRepository)
                .save(refreshToken);
    }

    @Test
    void logout_shouldRevokeRefreshToken() {

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setRevoked(false);

        when(refreshTokenService.findValid(
                "refresh-token"))
                .thenReturn(refreshToken);

        LogoutRequest request =
                new LogoutRequest(
                        "refresh-token"
                );

        userService.logout(
                request,
                "1"
        );

        assertTrue(refreshToken.isRevoked());
    }

    @Test
    void logout_shouldFailWhenTokenBelongsToDifferentUser() {

        User anotherUser = User.builder()
                .id(99L)
                .name("Another User")
                .email("another@example.com")
                .role("USER")
                .build();

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setUser(anotherUser);
        refreshToken.setRevoked(false);

        when(refreshTokenService.findValid(
                "refresh-token"))
                .thenReturn(refreshToken);

        LogoutRequest request =
                new LogoutRequest(
                        "refresh-token"
                );

        assertThrows(
                RuntimeException.class,
                () -> userService.logout(
                        request,
                        "1"
                )
        );
    }
}
