package com.hector.encurtadorlink.controller;

import com.hector.encurtadorlink.dto.request.CreateUrlRequest;
import com.hector.encurtadorlink.dto.response.UrlResponse;
import com.hector.encurtadorlink.exception.UrlExpiredException;
import com.hector.encurtadorlink.exception.UrlNotFoundException;
import com.hector.encurtadorlink.model.Url;
import com.hector.encurtadorlink.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("UrlController Tests")
@ExtendWith(MockitoExtension.class)
class UrlControllerTest {

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlController urlController;

    private CreateUrlRequest createUrlRequest;
    private UrlResponse urlResponse;
    private Url url;

    @BeforeEach
    void setUp() {
        createUrlRequest = new CreateUrlRequest(
            "https://www.google.com",
            LocalDateTime.now().plusHours(1)
        );

        urlResponse = new UrlResponse(
            "https://www.google.com",
            "abc123",
            "https://dominio.com/abc123",
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(1)
        );

        url = new Url(
            "https://www.google.com",
            "abc123",
            LocalDateTime.now().plusHours(1)
        );
    }

    @Test
    @DisplayName("Should save URL and return response")
    void testSaveUrlShouldReturnResponse() {
        when(urlService.save(any(CreateUrlRequest.class))).thenReturn(urlResponse);

        var response = urlController.saveUrl(createUrlRequest);

        assertNotNull(response);
        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("abc123", response.getBody().shortCode());

        verify(urlService, times(1)).save(any(CreateUrlRequest.class));
    }

    @Test
    @DisplayName("Should get short code and redirect")
    void testGetShortCodeShouldReturnRedirect() {
        when(urlService.findByShortCode("abc123")).thenReturn(url);

        var response = urlController.getShortCode("abc123");

        assertEquals(302, response.getStatusCode().value());
        assertNotNull(response.getHeaders().getLocation());

        verify(urlService, times(1)).findByShortCode("abc123");
    }

    @Test
    @DisplayName("Should throw exception when URL not found")
    void testGetShortCodeShouldThrowWhenNotFound() {
        when(urlService.findByShortCode("invalid")).thenThrow(new UrlNotFoundException("url nao encontrada"));

        assertThrows(UrlNotFoundException.class, () -> {
            urlController.getShortCode("invalid");
        });

        verify(urlService, times(1)).findByShortCode("invalid");
    }

    @Test
    @DisplayName("Should throw exception when URL is expired")
    void testGetShortCodeShouldThrowWhenExpired() {
        when(urlService.findByShortCode("expired")).thenThrow(new UrlExpiredException("url expirou"));

        assertThrows(UrlExpiredException.class, () -> {
            urlController.getShortCode("expired");
        });

        verify(urlService, times(1)).findByShortCode("expired");
    }

    @Test
    @DisplayName("Should delete URL and return no content")
    void testDeleteUrlShouldReturn204() {
        doNothing().when(urlService).delete(1L);

        var response = urlController.deleteUrl(1L);

        assertEquals(204, response.getStatusCode().value());

        verify(urlService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Should call service with correct parameters")
    void testSaveUrlShouldCallServiceWithCorrectData() {
        when(urlService.save(any(CreateUrlRequest.class))).thenReturn(urlResponse);

        urlController.saveUrl(createUrlRequest);

        verify(urlService, times(1)).save(any(CreateUrlRequest.class));
    }
}
