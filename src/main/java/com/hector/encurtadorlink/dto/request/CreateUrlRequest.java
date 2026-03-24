package com.hector.encurtadorlink.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

public record CreateUrlRequest(@NotBlank @URL String originalUrl, @Future LocalDateTime expiresAt) {
}
