package com.hector.encurtadorlink.service;

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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("AuthService Tests")
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User user;
    private String token;

    @BeforeEach
    void setUp() {
        user = new User(
            "test@example.com",
            "Test User",
            "hashed_password",
            User.Role.USER
        );
        token = "jwt_token_example";
    }

    @Test
    @DisplayName("Should login successfully with correct credentials")
    void testLoginWithCorrectCredentialsShouldReturnToken() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn(token);

        String result = authService.login("test@example.com", "password123");

        assertNotNull(result);
        assertEquals(token, result);

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("password123", user.getPassword());
        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testLoginWithNonExistentUserShouldThrow() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            authService.login("nonexistent@example.com", "password123");
        });

        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Should throw exception with incorrect password")
    void testLoginWithIncorrectPasswordShouldThrow() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", user.getPassword())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            authService.login("test@example.com", "wrongpassword");
        });

        verify(userRepository, times(1)).findByEmail("test@example.com");
        verify(passwordEncoder, times(1)).matches("wrongpassword", user.getPassword());
    }

    @Test
    @DisplayName("Should validate password using encoder")
    void testLoginShouldValidatePasswordWithEncoder() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn(token);

        authService.login("test@example.com", "password123");

        verify(passwordEncoder, times(1)).matches("password123", user.getPassword());
    }

    @Test
    @DisplayName("Should generate token after successful login")
    void testLoginShouldGenerateTokenAfterValidation() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn(token);

        String result = authService.login("test@example.com", "password123");

        verify(jwtService, times(1)).generateToken(user);
        assertEquals(token, result);
    }
}
