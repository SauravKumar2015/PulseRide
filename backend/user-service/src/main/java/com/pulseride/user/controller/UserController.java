package com.pulseride.user.controller;

import com.pulseride.user.dto.UserCreateRequest;
import com.pulseride.user.dto.UserResponse;
import com.pulseride.user.dto.UserUpdateRequest;
import com.pulseride.user.service.UserService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

        private final UserService userService;

        /*
         * Health check
         */
        @GetMapping("/health")
        public ResponseEntity<String> health() {

                return ResponseEntity.ok(
                                "USER-SERVICE is running");
        }

        /*
         * Create user profile
         *
         * Intended mainly for auth-service/internal integration.
         */
        @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<UserResponse> createUser(
                        @Valid @RequestBody UserCreateRequest request) {

                UserResponse response = userService.createUser(request);

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(response);
        }

        /*
         * Get currently authenticated user's profile
         */
        @GetMapping("/me")
        @PreAuthorize("hasRole('USER')")
        public ResponseEntity<UserResponse> getMyProfile(
                Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                userService.getUserById(userId)
        );
        }

        /*
         * Update currently authenticated user's profile
         */
        @PutMapping("/me")
        @PreAuthorize("hasRole('USER')")
        public ResponseEntity<UserResponse> updateMyProfile(
                Authentication authentication,
                @Valid @RequestBody UserUpdateRequest request) {

        Long userId = getAuthenticatedUserId(authentication);

        return ResponseEntity.ok(
                userService.updateUser(userId, request)
        );
        }

        /*
         * Delete currently authenticated user's account
         */
        @DeleteMapping("/me")
        @PreAuthorize("hasRole('USER')")
        public ResponseEntity<Void> deleteMyProfile(
                Authentication authentication) {

        Long userId = getAuthenticatedUserId(authentication);

        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
        }

        /*
         * Get user by ID
         *
         * ADMIN only.
         */
        @GetMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<UserResponse> getUser(
                        @PathVariable Long id) {

                return ResponseEntity.ok(
                                userService.getUserById(id));
        }

        /*
         * Get all users
         *
         * ADMIN only.
         */
        @GetMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<UserResponse>> getAllUsers() {

                return ResponseEntity.ok(
                                userService.getAllUsers());
        }

        /*
         * Get user by email
         *
         * ADMIN only.
         */
        @GetMapping("/email/{email}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<UserResponse> getByEmail(
                        @PathVariable String email) {

                return ResponseEntity.ok(
                                userService.getUserByEmail(email));
        }

        /*
         * Update user by ID
         *
         * ADMIN only.
         */
        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<UserResponse> updateUser(
                        @PathVariable Long id,
                        @Valid @RequestBody UserUpdateRequest request) {

                return ResponseEntity.ok(
                                userService.updateUser(id, request));
        }

        /*
         * Delete user by ID
         *
         * ADMIN only.
         */
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> deleteUser(
                        @PathVariable Long id) {

                userService.deleteUser(id);

                return ResponseEntity.noContent().build();
        }

        /*
         * Deactivate user
         *
         * ADMIN only.
         */
        @PatchMapping("/{id}/deactivate")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> deactivateUser(
                        @PathVariable Long id) {

                userService.deactivateUser(id);

                return ResponseEntity.noContent().build();
        }

        /*
         * Activate user
         *
         * ADMIN only.
         */
        @PatchMapping("/{id}/activate")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> activateUser(
                        @PathVariable Long id) {

                userService.activateUser(id);

                return ResponseEntity.noContent().build();
        }

        private Long getAuthenticatedUserId(
                        Authentication authentication) {

                if (authentication == null ||
                                authentication.getName() == null) {

                        throw new IllegalArgumentException(
                                        "Authenticated user ID is missing");
                }

                try {

                        return Long.parseLong(
                                        authentication.getName());

                } catch (NumberFormatException ex) {

                        throw new IllegalArgumentException(
                                        "Invalid authenticated user ID");
                }
        }
}