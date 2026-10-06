package com.pulseride.user.service;

import com.pulseride.user.dto.UserCreateRequest;
import com.pulseride.user.dto.UserResponse;
import com.pulseride.user.dto.UserUpdateRequest;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);

    List<UserResponse> getAllUsers();

    void deactivateUser(Long id);

    void activateUser(Long id);
}