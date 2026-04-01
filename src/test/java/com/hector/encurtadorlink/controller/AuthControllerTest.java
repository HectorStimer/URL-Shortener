package com.hector.encurtadorlink.controller;

import com.hector.encurtadorlink.dto.request.CreateUserRequest;
import com.hector.encurtadorlink.dto.request.LoginRequest;
import com.hector.encurtadorlink.dto.response.AuthResponse;
import com.hector.encurtadorlink.dto.response.UserResponseDTO;
import com.hector.encurtadorlink.model.User;
import com.hector.encurtadorlink.service.AuthService;
import com.hector.encurtadorlink.service.JwtService;
import com.hector.encurtadorlink.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("AuthController Tests")
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    private CreateUserRequest createUserRequest;
    private LoginRequest loginRequest;
    private UserResponseDTO userResponseDTO;
    private String token;

    @BeforeEach
    void setUp() {
        createUserRequest = new CreateUserRequest(
            "test@example.com",
            "Test User",
            "password123"
        );

        loginRequest = new LoginRequest("test@example.com", "password123");

        userResponseDTO = new UserResponseDTO(
            1L,
            "test@example.com",
            "Test User",
            User.Role.USER
        );

        token = "jwt_token_example";
    }

    @Test
    @DisplayName("Should register user and return token")
    void testRegisterShouldReturnToken() {
        when(userService.create(any(CreateUserRequest.class))).thenReturn(userResponseDTO);
        when(jwtService.generateToken(any(User.class))).thenReturn(token);

        var response = authController.register(createUserRequest);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(token, response.getBody().token());

        verify(userService, times(1)).create(any(CreateUserRequest.class));
        verify(jwtService, times(1)).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should login user and return token")
    void testLoginShouldReturnToken() {
        when(authService.login("test@example.com", "password123")).thenReturn(token);

        var response = authController.login(loginRequest);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(token, response.getBody().token());

        verify(authService, times(1)).login("test@example.com", "password123");
    }

    @Test
    @DisplayName("Should throw exception when user already exists")
    void testRegisterWithExistingEmailShouldThrow() {
        when(userService.create(any(CreateUserRequest.class)))
            .thenThrow(new RuntimeException("Email já cadastrado"));

        assertThrows(RuntimeException.class, () -> {
            authController.register(createUserRequest);
        });

        verify(userService, times(1)).create(any(CreateUserRequest.class));
    }

    @Test
    @DisplayName("Should throw exception when login credentials are invalid")
    void testLoginWithInvalidCredentialsShouldThrow() {
        when(authService.login(anyString(), anyString()))
            .thenThrow(new RuntimeException("Invalid credentials"));

        assertThrows(RuntimeException.class, () -> {
            authController.login(loginRequest);
        });

        verify(authService, times(1)).login("test@example.com", "password123");
    }

    @Test
    @DisplayName("Should call services with correct parameters on register")
    void testRegisterShouldCallServicesWithCorrectData() {
        when(userService.create(any(CreateUserRequest.class))).thenReturn(userResponseDTO);
        when(jwtService.generateToken(any(User.class))).thenReturn(token);

        authController.register(createUserRequest);

        verify(userService, times(1)).create(any(CreateUserRequest.class));
        verify(jwtService, times(1)).generateToken(any(User.class));
    }

    @Test
    @DisplayName("Should call AuthService login method")
    void testLoginShouldCallAuthService() {
        when(authService.login(anyString(), anyString())).thenReturn(token);

        authController.login(loginRequest);

        verify(authService, times(1)).login("test@example.com", "password123");
    }
}
