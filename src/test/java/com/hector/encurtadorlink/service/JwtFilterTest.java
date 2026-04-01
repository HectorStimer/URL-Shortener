package com.hector.encurtadorlink.service;

import com.hector.encurtadorlink.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("JwtFilter Tests")
@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtFilter jwtFilter;
    private User user;

    @BeforeEach
    void setUp() {
        jwtFilter = new JwtFilter(jwtService, userDetailsService);
        user = new User(
            "test@example.com",
            "Test User",
            "password123",
            User.Role.USER
        );
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should pass filter chain when no Authorization header")
    void testDoFilterInternalWithoutAuthHeaderShouldCallFilterChain() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtService, never()).extractUsername(anyString());
    }

    @Test
    @DisplayName("Should pass filter chain when Authorization header doesn't start with Bearer")
    void testDoFilterInternalWithoutBearerPrefixShouldCallFilterChain() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Basic token");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should set authentication when token is valid")
    void testDoFilterInternalWithValidTokenShouldSetAuthentication() throws ServletException, IOException {
        String token = "valid_token";
        String header = "Bearer " + token;

        when(request.getHeader("Authorization")).thenReturn(header);
        when(jwtService.extractUsername(token)).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(user);
        when(jwtService.isTokenValid(token, user)).thenReturn(true);

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("test@example.com", SecurityContextHolder.getContext().getAuthentication().getName());

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not set authentication when token is invalid")
    void testDoFilterInternalWithInvalidTokenShouldNotSetAuthentication() throws ServletException, IOException {
        String token = "invalid_token";
        String header = "Bearer " + token;

        when(request.getHeader("Authorization")).thenReturn(header);
        when(jwtService.extractUsername(token)).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(user);
        when(jwtService.isTokenValid(token, user)).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should continue filter chain even on exception")
    void testDoFilterInternalShouldContinueOnException() throws ServletException, IOException {
        String token = "bad_token";
        String header = "Bearer " + token;

        when(request.getHeader("Authorization")).thenReturn(header);
        when(jwtService.extractUsername(token)).thenThrow(new RuntimeException("Invalid token"));

        assertDoesNotThrow(() -> {
            jwtFilter.doFilterInternal(request, response, filterChain);
        });

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should extract token correctly from Bearer header")
    void testDoFilterInternalShouldExtractTokenCorrectly() throws ServletException, IOException {
        String token = "extracted_token";
        String header = "Bearer " + token;

        when(request.getHeader("Authorization")).thenReturn(header);
        when(jwtService.extractUsername(token)).thenReturn("test@example.com");
        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(user);
        when(jwtService.isTokenValid(token, user)).thenReturn(true);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, times(1)).extractUsername(token);
    }

    @Test
    @DisplayName("Should handle null user email from token")
    void testDoFilterInternalShouldHandleNullUserEmail() throws ServletException, IOException {
        String token = "valid_token";
        String header = "Bearer " + token;

        when(request.getHeader("Authorization")).thenReturn(header);
        when(jwtService.extractUsername(token)).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }
}
