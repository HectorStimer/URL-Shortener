package com.hector.encurtadorlink.dto.response;

import java.time.LocalDateTime;

public record UrlResponse(
        String originalUrl,
        String shortCode,
        String shortUrl,
        LocalDateTime createdAt,
        LocalDateTime expiresAt) {
}
