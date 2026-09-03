package com.pulseride.auth.service;

import java.time.LocalDateTime;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pulseride.auth.dto.LoginRequest;
import com.pulseride.auth.dto.LogoutRequest;
import com.pulseride.auth.dto.RefreshTokenRequest;
import com.pulseride.auth.dto.RegisterRequest;
import com.pulseride.auth.dto.TokenResponse;
import com.pulseride.auth.dto.UserResponse;
import com.pulseride.auth.entity.User;
import com.pulseride.auth.entity.RefreshToken;
import com.pulseride.auth.exception.UserAlreadyExistsException;
import com.pulseride.auth.exception.InvalidRefreshTokenException;
import com.pulseride.auth.repository.RefreshTokenRepository;
import com.pulseride.auth.repository.UserRepository;
import com.pulseride.auth.security.JwtService;
import com.pulseride.auth.security.RefreshTokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole().trim().toUpperCase(Locale.ROOT))
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser;
        try {
            savedUser = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        return UserResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new BadCredentialsException("Authentication failed"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Authentication failed");
        }
        return issueTokens(user);
    }

    @Override
    @Transactional
    public TokenResponse refresh(RefreshTokenRequest request) {
        RefreshToken token = refreshTokenService.findValid(request.refreshToken());
        token.setRevoked(true);
        refreshTokenRepository.save(token);
        return issueTokens(token.getUser());
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request, String authenticatedUserId) {
        RefreshToken token = refreshTokenService.findValid(request.refreshToken());
        if (!token.getUser().getId().toString().equals(authenticatedUserId)) {
            throw new InvalidRefreshTokenException();
        }
        token.setRevoked(true);
    }

    private TokenResponse issueTokens(User user) {
        String accessToken = jwtService.createAccessToken(user);
        String refreshToken = refreshTokenService.create(user);
        return new TokenResponse(accessToken, refreshToken, "Bearer", jwtService.getAccessTokenExpirationSeconds());
    }
}