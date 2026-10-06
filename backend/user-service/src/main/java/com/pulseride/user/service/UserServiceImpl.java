package com.pulseride.user.service;

import com.pulseride.user.dto.UserCreateRequest;
import com.pulseride.user.dto.UserResponse;
import com.pulseride.user.dto.UserUpdateRequest;
import com.pulseride.user.entity.UserEntity;
import com.pulseride.user.exception.ResourceNotFoundException;
import com.pulseride.user.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.pulseride.user.entity.UserRole;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse createUser(UserCreateRequest request) {

        if (request.getRole() != UserRole.USER) {
        throw new IllegalArgumentException(
                "Only USER role profiles can be stored in user-service"
        );
        }

        if (userRepository.existsById(request.getId())) {
            throw new IllegalArgumentException(
                    "User already exists with ID: " + request.getId()
            );
        }

        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already exists: " + request.getEmail()
            );
        }

        UserEntity user = new UserEntity();

        user.setId(request.getId());
        user.setName(request.getName());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setActive(true);

        UserEntity savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + id
                        )
                );

        return mapToResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {

        UserEntity user = userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email
                        )
                );

        return mapToResponse(user);
    }

    @Override
    public UserResponse updateUser(
            Long id,
            UserUpdateRequest request) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + id
                        )
                );

        if (request.getName() != null &&
                !request.getName().isBlank()) {

            user.setName(request.getName().trim());
        }

        if (request.getEmail() != null &&
                !request.getEmail().isBlank()) {

            String newEmail =
                    request.getEmail().trim().toLowerCase();

            if (!newEmail.equalsIgnoreCase(user.getEmail())
                    && userRepository.existsByEmailIgnoreCase(newEmail)) {

                throw new IllegalArgumentException(
                        "Email already exists: " + newEmail
                );
            }

            user.setEmail(newEmail);
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(
                    request.getProfileImageUrl()
            );
        }

        return mapToResponse(
                userRepository.save(user)
        );
    }

    @Override
    public void deleteUser(Long id) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + id
                        )
                );

        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deactivateUser(Long id) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + id
                        )
                );

        user.setActive(false);

        userRepository.save(user);
    }

    @Override
    public void activateUser(Long id) {

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with ID: " + id
                        )
                );

        user.setActive(true);

        userRepository.save(user);
    }

    private UserResponse mapToResponse(UserEntity user) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getProfileImageUrl(),
                user.getActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}