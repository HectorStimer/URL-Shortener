package com.hector.encurtadorlink.controller;

import com.hector.encurtadorlink.dto.request.CreateUserRequest;
import com.hector.encurtadorlink.dto.response.UserResponseDTO;
import com.hector.encurtadorlink.model.User;
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
import static org.mockito.Mockito.*;

@DisplayName("UserController Tests")
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private CreateUserRequest createUserRequest;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        createUserRequest = new CreateUserRequest(
            "test@example.com",
            "Test User",
            "password123"
        );

        userResponseDTO = new UserResponseDTO(
            1L,
            "test@example.com",
            "Test User",
            User.Role.USER
        );
    }

    @Test
    @DisplayName("Should create user and return 201 Created")
    void testCreateUserShouldReturn201() {
        when(userService.create(any(CreateUserRequest.class))).thenReturn(userResponseDTO);

        var response = userController.create(createUserRequest);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("test@example.com", response.getBody().email());
        assertEquals("Test User", response.getBody().name());

        verify(userService, times(1)).create(any(CreateUserRequest.class));
    }

    @Test
    @DisplayName("Should return user data in response")
    void testCreateUserShouldReturnUserData() {
        when(userService.create(any(CreateUserRequest.class))).thenReturn(userResponseDTO);

        var response = userController.create(createUserRequest);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("test@example.com", response.getBody().email());

        verify(userService, times(1)).create(any(CreateUserRequest.class));
    }

    @Test
    @DisplayName("Should call service with correct parameters")
    void testCreateUserShouldCallServiceWithCorrectData() {
        when(userService.create(any(CreateUserRequest.class))).thenReturn(userResponseDTO);

        userController.create(createUserRequest);

        verify(userService, times(1)).create(any(CreateUserRequest.class));
    }

    @Test
    @DisplayName("Should throw exception when user creation fails")
    void testCreateUserShouldThrowOnError() {
        when(userService.create(any(CreateUserRequest.class)))
            .thenThrow(new RuntimeException("Email já cadastrado"));

        assertThrows(RuntimeException.class, () -> {
            userController.create(createUserRequest);
        });

        verify(userService, times(1)).create(any(CreateUserRequest.class));
    }

    @Test
    @DisplayName("Should include user role in response")
    void testCreateUserShouldIncludeRole() {
        when(userService.create(any(CreateUserRequest.class))).thenReturn(userResponseDTO);

        var response = userController.create(createUserRequest);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(User.Role.USER, response.getBody().role());

        verify(userService, times(1)).create(any(CreateUserRequest.class));
    }
}
