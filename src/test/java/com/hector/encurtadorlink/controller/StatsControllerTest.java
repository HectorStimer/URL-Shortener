package com.hector.encurtadorlink.controller;

import com.hector.encurtadorlink.dto.response.DailyClicksResponse;
import com.hector.encurtadorlink.dto.response.StatsResponse;
import com.hector.encurtadorlink.dto.response.UrlResponse;
import com.hector.encurtadorlink.exception.UrlExpiredException;
import com.hector.encurtadorlink.exception.UrlNotFoundException;
import com.hector.encurtadorlink.model.Url;
import com.hector.encurtadorlink.service.ClickService;
import com.hector.encurtadorlink.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("StatsController Tests")
@ExtendWith(MockitoExtension.class)
class StatsControllerTest {

    @Mock
    private ClickService clickService;

    @Mock
    private UrlService urlService;

    @InjectMocks
    private StatsController statsController;

    private Url url;
    private StatsResponse statsResponse;

    @BeforeEach
    void setUp() {
        url = new Url(
            "https://www.google.com",
            "abc123",
            LocalDateTime.now().plusHours(1)
        );

        UrlResponse urlResponse = new UrlResponse(
            "https://www.google.com",
            "abc123",
            "https://dominio.com/abc123",
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(1)
        );

        List<DailyClicksResponse> dailyClicks = new ArrayList<>();
        dailyClicks.add(new DailyClicksResponse(5, LocalDate.now()));
        dailyClicks.add(new DailyClicksResponse(3, LocalDate.now().minusDays(1)));

        statsResponse = new StatsResponse(urlResponse, 8, dailyClicks);
    }

    @Test
    @DisplayName("Should return stats for valid short code")
    void testGetStatisticsURLShouldReturnStats() {
        when(urlService.findByShortCode("abc123")).thenReturn(url);
        when(clickService.getStats(url)).thenReturn(statsResponse);

        var response = statsController.statisticsURL("abc123");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(8, response.getBody().totalClicks());
        assertEquals("abc123", response.getBody().url().shortCode());

        verify(urlService, times(1)).findByShortCode("abc123");
        verify(clickService, times(1)).getStats(url);
    }

    @Test
    @DisplayName("Should throw exception when short code not found")
    void testGetStatisticsURLShouldThrowNotFound() {
        when(urlService.findByShortCode("invalid")).thenThrow(new UrlNotFoundException("url nao encontrada"));

        assertThrows(UrlNotFoundException.class, () -> {
            statsController.statisticsURL("invalid");
        });

        verify(urlService, times(1)).findByShortCode("invalid");
    }

    @Test
    @DisplayName("Should throw exception when URL is expired")
    void testGetStatisticsURLShouldThrowExpired() {
        when(urlService.findByShortCode("expired")).thenThrow(new UrlExpiredException("url expirou"));

        assertThrows(UrlExpiredException.class, () -> {
            statsController.statisticsURL("expired");
        });

        verify(urlService, times(1)).findByShortCode("expired");
    }

    @Test
    @DisplayName("Should return stats with daily breakdown")
    void testGetStatisticsURLShouldReturnDailyClicks() {
        when(urlService.findByShortCode("abc123")).thenReturn(url);
        when(clickService.getStats(url)).thenReturn(statsResponse);

        var response = statsController.statisticsURL("abc123");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody().clicksPerDay());
        assertEquals(2, response.getBody().clicksPerDay().size());
        assertEquals(5, response.getBody().clicksPerDay().get(0).clicksPerDay());

        verify(clickService, times(1)).getStats(url);
    }

    @Test
    @DisplayName("Should include URL information in stats response")
    void testGetStatisticsURLShouldIncludeUrlData() {
        when(urlService.findByShortCode("abc123")).thenReturn(url);
        when(clickService.getStats(url)).thenReturn(statsResponse);

        var response = statsController.statisticsURL("abc123");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("https://www.google.com", response.getBody().url().originalUrl());
        assertEquals("abc123", response.getBody().url().shortCode());

        verify(urlService, times(1)).findByShortCode("abc123");
    }

    @Test
    @DisplayName("Should call services with correct short code")
    void testGetStatisticsURLShouldCallServicesWithCorrectCode() {
        when(urlService.findByShortCode("test123")).thenReturn(url);
        when(clickService.getStats(url)).thenReturn(statsResponse);

        var response = statsController.statisticsURL("test123");

        assertEquals(200, response.getStatusCode().value());
        verify(urlService, times(1)).findByShortCode("test123");
        verify(clickService, times(1)).getStats(url);
    }
}
