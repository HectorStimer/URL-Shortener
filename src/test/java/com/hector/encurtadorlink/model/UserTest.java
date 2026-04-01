package com.hector.encurtadorlink.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Model Tests")
class UserTest {

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
    @DisplayName("Should create User with correct values")
    void testCreateUserShouldInitializeCorrectly() {
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test User", user.getName());
        assertEquals("password123", user.getPassword());
        assertEquals(User.Role.USER, user.getRole());
    }

    @Test
    @DisplayName("Should implement UserDetails getUsername returning email")
    void testGetUsernameShouldReturnEmail() {
        assertEquals("test@example.com", user.getUsername());
    }

    @Test
    @DisplayName("Should return authorities with ROLE_USER")
    void testGetAuthoritiesShouldReturnUserRole() {
        assertTrue(user.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("Should return authorities with ROLE_ADMIN for admin user")
    void testGetAuthoritiesShouldReturnAdminRole() {
        User adminUser = new User(
            "admin@example.com",
            "Admin",
            "admin123",
            User.Role.ADMIN
        );
        assertTrue(adminUser.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Should have getPassword method")
    void testGetPasswordShouldReturnPassword() {
        assertEquals("password123", user.getPassword());
    }

    @Test
    @DisplayName("Should return true for isAccountNonExpired")
    void testIsAccountNonExpiredShouldReturnTrue() {
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    @DisplayName("Should return true for isAccountNonLocked")
    void testIsAccountNonLockedShouldReturnTrue() {
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    @DisplayName("Should return true for isCredentialsNonExpired")
    void testIsCredentialsNonExpiredShouldReturnTrue() {
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    @DisplayName("Should return true for isEnabled")
    void testIsEnabledShouldReturnTrue() {
        assertTrue(user.isEnabled());
    }

    @Test
    @DisplayName("Should have protected constructor for JPA")
    void testUserShouldHaveProtectedConstructor() {
        User emptyUser = new User();
        assertNotNull(emptyUser);
    }

    @Test
    @DisplayName("Should implement UserDetails interface")
    void testUserShouldImplementUserDetails() {
        assertTrue(user instanceof org.springframework.security.core.userdetails.UserDetails);
    }
}
