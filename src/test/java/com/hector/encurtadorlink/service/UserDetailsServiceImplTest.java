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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("UserDetailsServiceImpl Tests")
@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(
            "test@example.com",
            "Test User",
            "password123",
            User.Role.USER
        );
    }

    @Test
    @DisplayName("Should load user by username (email)")
    void testLoadUserByUsernameShouldReturnUserDetails() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetails result = userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testLoadUserByUsernameNotFoundShouldThrow() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent@example.com");
        });

        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Should return user with correct authorities")
    void testLoadUserByUsernameShouldReturnCorrectAuthorities() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetails result = userDetailsService.loadUserByUsername("test@example.com");

        assertNotNull(result.getAuthorities());
        assertFalse(result.getAuthorities().isEmpty());
        assertTrue(result.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should return user with password")
    void testLoadUserByUsernameShouldReturnPassword() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetails result = userDetailsService.loadUserByUsername("test@example.com");

        assertEquals("password123", result.getPassword());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should handle case-sensitive email lookup")
    void testLoadUserByUsernameWithDifferentCaseShouldSearchCorrectly() {
        when(userRepository.findByEmail("Test@Example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("Test@Example.com");
        });

        verify(userRepository, times(1)).findByEmail("Test@Example.com");
    }
}
