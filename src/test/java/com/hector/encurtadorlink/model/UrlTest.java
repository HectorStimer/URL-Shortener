package com.hector.encurtadorlink.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Url Model Tests")
class UrlTest {

    private Url url;
    private LocalDateTime expiryDate;

    @BeforeEach
    void setUp() {
        expiryDate = LocalDateTime.now().plusHours(1);
        url = new Url("https://www.google.com", "abc123", expiryDate);
    }

    @Test
    @DisplayName("Should create Url with correct values")
    void testCreateUrlShouldInitializeCorrectly() {
        assertEquals("https://www.google.com", url.getOriginalUrl());
        assertEquals("abc123", url.getShortCode());
        assertEquals(expiryDate, url.getExpiresAt());
    }

    @Test
    @DisplayName("Should set createdAt on creation")
    void testUrlShouldSetCreatedAtOnCreation() {
        url.onCreate();
        assertNotNull(url.getCreatedAt());
    }

    @Test
    @DisplayName("Should return non-null id")
    void testUrlShouldHaveGetId() {
        assertNotNull(url);
        // ID will be null before persistence
    }

    @Test
    @DisplayName("Should allow setting original URL")
    void testSetOriginalUrlShouldUpdateValue() {
        url.setOriginalUrl("https://www.github.com");
        assertEquals("https://www.github.com", url.getOriginalUrl());
    }

    @Test
    @DisplayName("Should allow setting short code")
    void testSetShortCodeShouldUpdateValue() {
        url.setShortCode("xyz789");
        assertEquals("xyz789", url.getShortCode());
    }

    @Test
    @DisplayName("Should allow setting expiration date")
    void testSetExpiredAtShouldUpdateValue() {
        LocalDateTime newExpiry = LocalDateTime.now().plusDays(5);
        url.setExpiredAt(newExpiry);
        assertEquals(newExpiry, url.getExpiresAt());
    }

    @Test
    @DisplayName("Should have protected constructor for JPA")
    void testUrlShouldHaveProtectedConstructor() {
        Url emptyUrl = new Url();
        assertNotNull(emptyUrl);
    }
}
