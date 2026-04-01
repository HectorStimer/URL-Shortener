package com.hector.encurtadorlink.service;

import com.hector.encurtadorlink.dto.request.CreateUrlRequest;
import com.hector.encurtadorlink.dto.response.UrlResponse;
import com.hector.encurtadorlink.exception.UrlExpiredException;
import com.hector.encurtadorlink.exception.UrlNotFoundException;
import com.hector.encurtadorlink.model.Url;
import com.hector.encurtadorlink.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("UrlService Tests")
@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    @InjectMocks
    private UrlService urlService;

    private CreateUrlRequest createUrlRequest;
    private Url url;

    @BeforeEach
    void setUp() {
        createUrlRequest = new CreateUrlRequest(
            "https://www.google.com",
            LocalDateTime.now().plusHours(1)
        );

        url = new Url(
            "https://www.google.com",
            "abc123",
            LocalDateTime.now().plusHours(1)
        );
    }

    @Test
    @DisplayName("Should save URL and return UrlResponse")
    void testSaveShouldReturnUrlResponse() {
        when(shortCodeGenerator.generate()).thenReturn("abc123");
        when(urlRepository.save(any(Url.class))).thenReturn(url);

        UrlResponse response = urlService.save(createUrlRequest);

        assertNotNull(response);
        assertEquals("https://www.google.com", response.originalUrl());
        assertEquals("abc123", response.shortCode());
        assertTrue(response.shortUrl().contains("abc123"));

        verify(shortCodeGenerator, times(1)).generate();
        verify(urlRepository, times(1)).save(any(Url.class));
    }

    @Test
    @DisplayName("Should find URL by short code")
    void testFindByShortCodeShouldReturnUrl() {
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(url));

        Url foundUrl = urlService.findByShortCode("abc123");

        assertNotNull(foundUrl);
        assertEquals("abc123", foundUrl.getShortCode());
        assertEquals("https://www.google.com", foundUrl.getOriginalUrl());

        verify(urlRepository, times(1)).findByShortCode("abc123");
    }

    @Test
    @DisplayName("Should throw UrlNotFoundException when URL not found")
    void testFindByShortCodeShouldThrowNotFound() {
        when(urlRepository.findByShortCode("invalid")).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> {
            urlService.findByShortCode("invalid");
        });

        verify(urlRepository, times(1)).findByShortCode("invalid");
    }

    @Test
    @DisplayName("Should throw UrlExpiredException when URL is expired")
    void testFindByShortCodeShouldThrowExpired() {
        Url expiredUrl = new Url(
            "https://www.google.com",
            "abc123",
            LocalDateTime.now().minusHours(1)  // Expired
        );

        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(expiredUrl));

        assertThrows(UrlExpiredException.class, () -> {
            urlService.findByShortCode("abc123");
        });

        verify(urlRepository, times(1)).findByShortCode("abc123");
    }

    @Test
    @DisplayName("Should delete URL by id")
    void testDeleteShouldCallRepository() {
        urlService.delete(1L);

        verify(urlRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should not throw exception when deleting non-existent URL")
    void testDeleteNonExistentUrlShouldNotThrow() {
        assertDoesNotThrow(() -> urlService.delete(999L));

        verify(urlRepository, times(1)).deleteById(999L);
    }
}
