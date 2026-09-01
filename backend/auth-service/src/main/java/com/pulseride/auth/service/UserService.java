package com.pulseride.auth.service;

import com.pulseride.auth.dto.RegisterRequest;
import com.pulseride.auth.dto.UserResponse;

public interface UserService {

    UserResponse registerUser(RegisterRequest request);
}
