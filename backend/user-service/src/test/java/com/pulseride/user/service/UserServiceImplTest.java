package com.pulseride.user.service;

import com.pulseride.user.dto.UserCreateRequest;
import com.pulseride.user.dto.UserResponse;
import com.pulseride.user.dto.UserUpdateRequest;
import com.pulseride.user.entity.UserEntity;
import com.pulseride.user.entity.UserRole;
import com.pulseride.user.exception.ResourceNotFoundException;
import com.pulseride.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity user;

    @BeforeEach
    void setUp() {

        user = new UserEntity();

        user.setId(1L);
        user.setName("Test User");
        user.setEmail("user@test.com");
        user.setPhone("9876543210");
        user.setRole(UserRole.USER);
        user.setActive(true);
        user.setProfileImageUrl(null);
    }

    // =========================================================
    // CREATE USER
    // =========================================================

    @Test
    void createUser_shouldCreateUserSuccessfully() {

        UserCreateRequest request = new UserCreateRequest();

        request.setId(1L);
        request.setName("Test User");
        request.setEmail("user@test.com");
        request.setPhone("9876543210");
        request.setRole(UserRole.USER);

        when(userRepository.existsById(1L))
                .thenReturn(false);

        when(userRepository.existsByEmailIgnoreCase("user@test.com"))
                .thenReturn(false);

        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(user);

        UserResponse response =
                userService.createUser(request);

        assertNotNull(response);

        assertEquals(1L, response.getId());
        assertEquals("Test User", response.getName());
        assertEquals("user@test.com", response.getEmail());
        assertEquals("9876543210", response.getPhone());
        assertEquals(UserRole.USER, response.getRole());
        assertTrue(response.getActive());

        verify(userRepository)
                .existsById(1L);

        verify(userRepository)
                .existsByEmailIgnoreCase("user@test.com");

        verify(userRepository)
                .save(any(UserEntity.class));
    }

    // =========================================================
    // CREATE USER - DRIVER SHOULD BE REJECTED
    // =========================================================

    @Test
    void createUser_shouldRejectDriverRole() {

        UserCreateRequest request = new UserCreateRequest();

        request.setId(2L);
        request.setName("Test Driver");
        request.setEmail("driver@test.com");
        request.setPhone("9876543211");
        request.setRole(UserRole.DRIVER);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.createUser(request)
                );

        assertEquals(
                "Only USER role profiles can be stored in user-service",
                exception.getMessage()
        );

        verify(userRepository, never())
                .existsById(anyLong());

        verify(userRepository, never())
                .existsByEmailIgnoreCase(anyString());

        verify(userRepository, never())
                .save(any(UserEntity.class));
    }

    // =========================================================
    // CREATE USER - ADMIN SHOULD BE REJECTED
    // =========================================================

    @Test
    void createUser_shouldRejectAdminRole() {

        UserCreateRequest request = new UserCreateRequest();

        request.setId(3L);
        request.setName("Test Admin");
        request.setEmail("admin@test.com");
        request.setPhone("9876543212");
        request.setRole(UserRole.ADMIN);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.createUser(request)
                );

        assertEquals(
                "Only USER role profiles can be stored in user-service",
                exception.getMessage()
        );

        verify(userRepository, never())
                .existsById(anyLong());

        verify(userRepository, never())
                .existsByEmailIgnoreCase(anyString());

        verify(userRepository, never())
                .save(any(UserEntity.class));
    }

    // =========================================================
    // CREATE USER - DUPLICATE ID
    // =========================================================

    @Test
    void createUser_shouldRejectDuplicateId() {

        UserCreateRequest request = new UserCreateRequest();

        request.setId(1L);
        request.setName("Another User");
        request.setEmail("another@test.com");
        request.setPhone("9876543213");
        request.setRole(UserRole.USER);

        when(userRepository.existsById(1L))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.createUser(request)
                );

        assertEquals(
                "User already exists with ID: 1",
                exception.getMessage()
        );

        verify(userRepository)
                .existsById(1L);

        verify(userRepository, never())
                .existsByEmailIgnoreCase(anyString());

        verify(userRepository, never())
                .save(any(UserEntity.class));
    }

    // =========================================================
    // CREATE USER - DUPLICATE EMAIL
    // =========================================================

    @Test
    void createUser_shouldRejectDuplicateEmail() {

        UserCreateRequest request = new UserCreateRequest();

        request.setId(2L);
        request.setName("Another User");
        request.setEmail("user@test.com");
        request.setPhone("9876543214");
        request.setRole(UserRole.USER);

        when(userRepository.existsById(2L))
                .thenReturn(false);

        when(userRepository.existsByEmailIgnoreCase("user@test.com"))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.createUser(request)
                );

        assertEquals(
                "Email already exists: user@test.com",
                exception.getMessage()
        );

        verify(userRepository)
                .existsById(2L);

        verify(userRepository)
                .existsByEmailIgnoreCase("user@test.com");

        verify(userRepository, never())
                .save(any(UserEntity.class));
    }

    // =========================================================
    // GET USER BY ID
    // =========================================================

    @Test
    void getUserById_shouldReturnUser() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserResponse response =
                userService.getUserById(1L);

        assertNotNull(response);

        assertEquals(1L, response.getId());
        assertEquals("Test User", response.getName());
        assertEquals("user@test.com", response.getEmail());
        assertEquals(UserRole.USER, response.getRole());
        assertTrue(response.getActive());

        verify(userRepository)
                .findById(1L);
    }

    // =========================================================
    // GET USER BY ID - NOT FOUND
    // =========================================================

    @Test
    void getUserById_shouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(999L)
        );

        verify(userRepository)
                .findById(999L);
    }

    // =========================================================
    // GET USER BY EMAIL
    // =========================================================

    @Test
    void getUserByEmail_shouldReturnUser() {

        when(userRepository.findByEmailIgnoreCase(
                "user@test.com"))
                .thenReturn(Optional.of(user));

        UserResponse response =
                userService.getUserByEmail(
                        "user@test.com"
                );

        assertNotNull(response);

        assertEquals(1L, response.getId());
        assertEquals("Test User", response.getName());
        assertEquals("user@test.com", response.getEmail());
        assertEquals(UserRole.USER, response.getRole());

        verify(userRepository)
                .findByEmailIgnoreCase("user@test.com");
    }

    // =========================================================
    // GET USER BY EMAIL - NOT FOUND
    // =========================================================

    @Test
    void getUserByEmail_shouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.findByEmailIgnoreCase(
                "unknown@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserByEmail(
                        "unknown@test.com"
                )
        );

        verify(userRepository)
                .findByEmailIgnoreCase("unknown@test.com");
    }

    // =========================================================
    // UPDATE USER
    // =========================================================

    @Test
    void updateUser_shouldUpdateUserSuccessfully() {

        UserUpdateRequest request =
                new UserUpdateRequest();

        request.setName("Updated User");
        request.setEmail("updated@test.com");
        request.setPhone("9999999999");
        request.setProfileImageUrl(
                "https://example.com/profile.jpg"
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmailIgnoreCase(
                "updated@test.com"))
                .thenReturn(false);

        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(user);

        UserResponse response =
                userService.updateUser(
                        1L,
                        request
                );

        assertNotNull(response);

        assertEquals(
                "Updated User",
                response.getName()
        );

        assertEquals(
                "updated@test.com",
                response.getEmail()
        );

        assertEquals(
                "9999999999",
                response.getPhone()
        );

        assertEquals(
                "https://example.com/profile.jpg",
                response.getProfileImageUrl()
        );

        verify(userRepository)
                .findById(1L);

        verify(userRepository)
                .existsByEmailIgnoreCase(
                        "updated@test.com"
                );

        verify(userRepository)
                .save(user);
    }

    // =========================================================
    // UPDATE USER - NOT FOUND
    // =========================================================

    @Test
    void updateUser_shouldThrowExceptionWhenUserDoesNotExist() {

        UserUpdateRequest request =
                new UserUpdateRequest();

        request.setName("Updated User");

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.updateUser(
                        999L,
                        request
                )
        );

        verify(userRepository)
                .findById(999L);

        verify(userRepository, never())
                .save(any(UserEntity.class));
    }

    // =========================================================
    // UPDATE USER - DUPLICATE EMAIL
    // =========================================================

    @Test
    void updateUser_shouldRejectDuplicateEmail() {

        UserUpdateRequest request =
                new UserUpdateRequest();

        request.setEmail("existing@test.com");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmailIgnoreCase(
                "existing@test.com"))
                .thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.updateUser(
                                1L,
                                request
                        )
                );

        assertEquals(
                "Email already exists: existing@test.com",
                exception.getMessage()
        );

        verify(userRepository)
                .findById(1L);

        verify(userRepository)
                .existsByEmailIgnoreCase(
                        "existing@test.com"
                );

        verify(userRepository, never())
                .save(any(UserEntity.class));
    }

    // =========================================================
    // DELETE USER
    // =========================================================

    @Test
    void deleteUser_shouldDeleteUserSuccessfully() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository)
                .findById(1L);

        verify(userRepository)
                .delete(user);
    }

    // =========================================================
    // DELETE USER - NOT FOUND
    // =========================================================

    @Test
    void deleteUser_shouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.deleteUser(999L)
        );

        verify(userRepository, never())
                .delete(any(UserEntity.class));
    }

    // =========================================================
    // GET ALL USERS
    // =========================================================

    @Test
    void getAllUsers_shouldReturnAllUsers() {

        UserEntity secondUser =
                new UserEntity();

        secondUser.setId(2L);
        secondUser.setName("Second User");
        secondUser.setEmail("second@test.com");
        secondUser.setPhone("8888888888");
        secondUser.setRole(UserRole.USER);
        secondUser.setActive(true);

        when(userRepository.findAll())
                .thenReturn(List.of(
                        user,
                        secondUser
                ));

        List<UserResponse> response =
                userService.getAllUsers();

        assertNotNull(response);

        assertEquals(2, response.size());

        assertEquals(
                1L,
                response.get(0).getId()
        );

        assertEquals(
                "Test User",
                response.get(0).getName()
        );

        assertEquals(
                2L,
                response.get(1).getId()
        );

        assertEquals(
                "Second User",
                response.get(1).getName()
        );

        verify(userRepository)
                .findAll();
    }

    // =========================================================
    // DEACTIVATE USER
    // =========================================================

    @Test
    void deactivateUser_shouldDeactivateUser() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.deactivateUser(1L);

        assertFalse(user.getActive());

        verify(userRepository)
                .findById(1L);

        verify(userRepository)
                .save(user);
    }

    // =========================================================
    // DEACTIVATE USER - NOT FOUND
    // =========================================================

    @Test
    void deactivateUser_shouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.deactivateUser(999L)
        );

        verify(userRepository, never())
                .save(any(UserEntity.class));
    }

    // =========================================================
    // ACTIVATE USER
    // =========================================================

    @Test
    void activateUser_shouldActivateUser() {

        user.setActive(false);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.activateUser(1L);

        assertTrue(user.getActive());

        verify(userRepository)
                .findById(1L);

        verify(userRepository)
                .save(user);
    }

    // =========================================================
    // ACTIVATE USER - NOT FOUND
    // =========================================================

    @Test
    void activateUser_shouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.activateUser(999L)
        );

        verify(userRepository, never())
                .save(any(UserEntity.class));
    }
}