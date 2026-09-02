package com.pulseride.auth.service;

import com.pulseride.auth.dto.RegisterRequest;
import com.pulseride.auth.dto.LoginRequest;
import com.pulseride.auth.dto.LogoutRequest;
import com.pulseride.auth.dto.RefreshTokenRequest;
import com.pulseride.auth.dto.TokenResponse;
import com.pulseride.auth.dto.UserResponse;

public interface UserService {

    UserResponse register(RegisterRequest request);
    TokenResponse login(LoginRequest request);
    TokenResponse refresh(RefreshTokenRequest request);
    void logout(LogoutRequest request, String authenticatedUserId);
}
