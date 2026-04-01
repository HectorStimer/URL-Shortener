package com.hector.encurtadorlink.service;

import com.hector.encurtadorlink.dto.request.CreateUserRequest;
import com.hector.encurtadorlink.dto.response.UserResponseDTO;
import com.hector.encurtadorlink.exception.BusinessException;
import com.hector.encurtadorlink.model.User;
import com.hector.encurtadorlink.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("UserService Tests")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private CreateUserRequest createUserRequest;
    private User user;

    @BeforeEach
    void setUp() {
        createUserRequest = new CreateUserRequest(
            "test@example.com",
            "Test User",
            "password123"
        );

        user = new User(
            "test@example.com",
            "Test User",
            "hashed_password",
            User.Role.USER
        );
    }

    @Test
    @DisplayName("Should create user with valid data")
    void testCreateUserShouldReturnUserResponseDTO() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO response = userService.create(createUserRequest);

        assertNotNull(response);
        assertEquals("test@example.com", response.email());
        assertEquals("Test User", response.name());

        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email is null")
    void testCreateUserWithNullEmailShouldThrow() {
        CreateUserRequest invalidRequest = new CreateUserRequest(null, "Test", "password");

        assertThrows(BusinessException.class, () -> {
            userService.create(invalidRequest);
        });
    }

    @Test
    @DisplayName("Should throw exception when email is blank")
    void testCreateUserWithBlankEmailShouldThrow() {
        CreateUserRequest invalidRequest = new CreateUserRequest("", "Test", "password");

        assertThrows(BusinessException.class, () -> {
            userService.create(invalidRequest);
        });
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void testCreateUserWithDuplicateEmailShouldThrow() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThrows(BusinessException.class, () -> {
            userService.create(createUserRequest);
        });

        verify(userRepository, times(1)).existsByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should validate email successfully with valid email")
    void testValidateEmailWithValidEmailShouldNotThrow() {
        when(userRepository.existsByEmail("valid@example.com")).thenReturn(false);

        assertDoesNotThrow(() -> {
            userService.validateEmail("valid@example.com");
        });
    }

    @Test
    @DisplayName("Should throw exception when password is encoded")
    void testCreateUserShouldEncodePassword() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encrypted_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.create(createUserRequest);

        verify(passwordEncoder, times(1)).encode("password123");
    }
}
