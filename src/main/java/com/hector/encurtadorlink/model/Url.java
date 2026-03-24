package com.hector.encurtadorlink.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="urls")
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String originalUrl;
    private String shortCode;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    protected Url(){}

    public Url(String originalUrl, String shortCode, LocalDateTime expiresAt){
        this.originalUrl=originalUrl;
        this.shortCode=shortCode;

        this.expiresAt=expiresAt;
    }

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }


    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }



    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }


    public void setExpiredAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
