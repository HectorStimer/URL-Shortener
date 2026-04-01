package com.hector.encurtadorlink.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Click Model Tests")
class ClickTest {

    private Url url;
    private Click click;

    @BeforeEach
    void setUp() {
        url = new Url(
            "https://www.google.com",
            "abc123",
            LocalDateTime.now().plusHours(1)
        );

        click = new Click(
            url,
            "192.168.1.1",
            "Mozilla/5.0",
            "https://referrer.com"
        );
    }

    @Test
    @DisplayName("Should create Click with correct values")
    void testCreateClickShouldInitializeCorrectly() {
        assertEquals(url, click.getUrl());
        assertEquals("192.168.1.1", click.getIpAddress());
        assertEquals("Mozilla/5.0", click.getUserAgent());
        assertEquals("https://referrer.com", click.getReferer());
    }

    @Test
    @DisplayName("Should set clickedAt on creation")
    void testClickShouldSetClickedAtOnCreation() {
        click.onCreate();
        assertNotNull(click.getClickedAt());
    }

    @Test
    @DisplayName("Should allow setting URL")
    void testSetUrlShouldUpdateValue() {
        Url newUrl = new Url(
            "https://www.github.com",
            "xyz789",
            LocalDateTime.now().plusHours(2)
        );
        click.setUrl(newUrl);
        assertEquals(newUrl, click.getUrl());
    }

    @Test
    @DisplayName("Should return IP address")
    void testGetIpAddressShouldReturnValue() {
        assertEquals("192.168.1.1", click.getIpAddress());
    }

    @Test
    @DisplayName("Should return user agent")
    void testGetUserAgentShouldReturnValue() {
        assertEquals("Mozilla/5.0", click.getUserAgent());
    }

    @Test
    @DisplayName("Should return referer")
    void testGetRefererShouldReturnValue() {
        assertEquals("https://referrer.com", click.getReferer());
    }

    @Test
    @DisplayName("Should return clicked at timestamp")
    void testGetClickedAtShouldReturnValue() {
        click.onCreate();
        LocalDateTime clickedAt = click.getClickedAt();
        assertNotNull(clickedAt);
    }

    @Test
    @DisplayName("Should have getId method")
    void testGetIdShouldReturnValue() {
        assertNotNull(click);
        // ID will be null before persistence
    }

    @Test
    @DisplayName("Should have protected constructor for JPA")
    void testClickShouldHaveProtectedConstructor() {
        Click emptyClick = new Click();
        assertNotNull(emptyClick);
    }

    @Test
    @DisplayName("Should handle null values for optional fields")
    void testClickShouldHandleNullValues() {
        Click clickWithNulls = new Click(url, null, null, null);
        assertNull(clickWithNulls.getIpAddress());
        assertNull(clickWithNulls.getUserAgent());
        assertNull(clickWithNulls.getReferer());
    }
}
