package com.hector.encurtadorlink.service;

import com.hector.encurtadorlink.model.User;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtService Tests")
@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(
            "test@example.com",
            "Test User",
            "password123",
            User.Role.USER
        );
        
        // Set JWT configuration via reflection since it's from @Value
        ReflectionTestUtils.setField(jwtService, "SECRET_KEY", 
            "testSecretKeyForJwtTestingPurposesOnlyTestSecretKeyForJwtTestingPurposesOnly");
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L);
    }

    @Test
    @DisplayName("Should generate valid token")
    void testGenerateTokenShouldReturnNonNullToken() {
        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertFalse(token.isBlank());
        assertTrue(token.contains("."));
    }

    @Test
    @DisplayName("Should extract username from token")
    void testExtractUsernameShouldReturnCorrectEmail() {
        String token = jwtService.generateToken(user);
        String extractedEmail = jwtService.extractUsername(token);

        assertEquals("test@example.com", extractedEmail);
    }

    @Test
    @DisplayName("Should return false for non-expired token")
    void testIsTokenExpiredShouldReturnFalseForValidToken() {
        String token = jwtService.generateToken(user);
        boolean isExpired = jwtService.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    @DisplayName("Should validate token with correct user details")
    void testIsTokenValidShouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(user);
        boolean isValid = jwtService.isTokenValid(token, user);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should return false for token with different user")
    void testIsTokenValidShouldReturnFalseForDifferentUser() {
        String token = jwtService.generateToken(user);
        
        User differentUser = new User(
            "different@example.com",
            "Different User",
            "password123",
            User.Role.USER
        );

        boolean isValid = jwtService.isTokenValid(token, differentUser);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should throw exception for invalid token format")
    void testExtractUsernameWithInvalidTokenShouldThrow() {
        assertThrows(JwtException.class, () -> {
            jwtService.extractUsername("invalid.token.format");
        });
    }

    @Test
    @DisplayName("Should contain subject as email in token")
    void testTokenShouldContainEmailAsSubject() {
        String token = jwtService.generateToken(user);
        String username = jwtService.extractUsername(token);

        assertEquals(user.getEmail(), username);
        assertEquals(user.getUsername(), username);
    }
}
