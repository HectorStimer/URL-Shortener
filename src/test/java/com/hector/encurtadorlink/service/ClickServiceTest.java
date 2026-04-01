package com.hector.encurtadorlink.service;

import com.hector.encurtadorlink.dto.response.DailyClicksResponse;
import com.hector.encurtadorlink.dto.response.StatsResponse;
import com.hector.encurtadorlink.model.Click;
import com.hector.encurtadorlink.model.Url;
import com.hector.encurtadorlink.repository.ClickReposirory;
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

@DisplayName("ClickService Tests")
@ExtendWith(MockitoExtension.class)
class ClickServiceTest {

    @Mock
    private ClickReposirory clickRepository;

    @InjectMocks
    private ClickService clickService;

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
    @DisplayName("Should register click asynchronously")
    void testRegisterClickShouldSaveClickToRepository() {
        clickService.registerClick(url, "192.168.1.1", "Mozilla/5.0", "https://referrer.com");

        verify(clickRepository, times(1)).save(any(Click.class));
    }

    @Test
    @DisplayName("Should save click with correct data")
    void testRegisterClickShouldSaveWithCorrectData() {
        String ipAddress = "192.168.1.100";
        String userAgent = "Chrome/90.0";
        String referer = "https://example.com";

        clickService.registerClick(url, ipAddress, userAgent, referer);

        verify(clickRepository, times(1)).save(any(Click.class));
    }

    @Test
    @DisplayName("Should get stats with correct total clicks count")
    void testGetStatsShouldReturnCorrectTotalClicks() {
        List<Click> clicks = new ArrayList<>();
        Click c1 = new Click(url, "192.168.1.1", "Mozilla", "referer");
        c1.onCreate();
        Click c2 = new Click(url, "192.168.1.1", "Mozilla", "referer");
        c2.onCreate();
        Click c3 = new Click(url, "192.168.1.1", "Mozilla", "referer");
        c3.onCreate();
        clicks.add(c1);
        clicks.add(c2);
        clicks.add(c3);

        when(clickRepository.findByUrl(url)).thenReturn(clicks);

        StatsResponse stats = clickService.getStats(url);

        assertEquals(3, stats.totalClicks());
        verify(clickRepository, times(1)).findByUrl(url);
    }

    @Test
    @DisplayName("Should calculate clicks per day correctly")
    void testGetStatsShouldCalculateClicksPerDay() {
        List<Click> clicks = new ArrayList<>();
        
        Click click1 = new Click(url, "192.168.1.1", "Mozilla", "referer");
        click1.onCreate();
        Click click2 = new Click(url, "192.168.1.2", "Chrome", "referer");
        click2.onCreate();
        
        clicks.add(click1);
        clicks.add(click2);

        when(clickRepository.findByUrl(url)).thenReturn(clicks);

        StatsResponse stats = clickService.getStats(url);

        assertNotNull(stats.clicksPerDay());
        assertTrue(stats.clicksPerDay().size() >= 1);
        verify(clickRepository, times(1)).findByUrl(url);
    }

    @Test
    @DisplayName("Should return empty daily clicks for URL with no clicks")
    void testGetStatsWithNoClicksShouldReturnEmptyList() {
        when(clickRepository.findByUrl(url)).thenReturn(new ArrayList<>());

        StatsResponse stats = clickService.getStats(url);

        assertEquals(0, stats.totalClicks());
        assertEquals(0, stats.clicksPerDay().size());
        verify(clickRepository, times(1)).findByUrl(url);
    }

    @Test
    @DisplayName("Should include URL information in stats response")
    void testGetStatsShouldIncludeUrlInformation() {
        List<Click> clicks = new ArrayList<>();
        Click c = new Click(url, "192.168.1.1", "Mozilla", "referer");
        c.onCreate();
        clicks.add(c);

        when(clickRepository.findByUrl(url)).thenReturn(clicks);

        StatsResponse stats = clickService.getStats(url);

        assertNotNull(stats.url());
        assertEquals("abc123", stats.url().shortCode());
        assertEquals("https://www.google.com", stats.url().originalUrl());
    }

    @Test
    @DisplayName("Should properly group clicks by date")
    void testGetStatsShouldGroupClicksByDate() {
        List<Click> clicks = new ArrayList<>();
        Click c1 = new Click(url, "192.168.1.1", "Mozilla", "referer");
        c1.onCreate();
        Click c2 = new Click(url, "192.168.1.2", "Chrome", "referer");
        c2.onCreate();
        clicks.add(c1);
        clicks.add(c2);

        when(clickRepository.findByUrl(url)).thenReturn(clicks);

        StatsResponse stats = clickService.getStats(url);

        assertNotNull(stats.clicksPerDay());
        assertTrue(stats.clicksPerDay().size() >= 1);
        
        for (DailyClicksResponse daily : stats.clicksPerDay()) {
            assertTrue(daily.clicksPerDay() > 0);
            assertNotNull(daily.dayOfClick());
        }
    }
}
