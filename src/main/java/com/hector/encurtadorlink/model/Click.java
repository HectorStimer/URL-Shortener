package com.hector.encurtadorlink.model;


import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name= "clicks")
public class Click {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name= "id_url")
    private Url url;

    private LocalDateTime clickedAt;
    private String ipAddress;
    private String userAgent;
    private String referer;

    protected Click(){}

    public Click( Url url, String ipAddress, String userAgent, String referer){
        this.url=url;
        this.ipAddress=ipAddress;
        this.userAgent=userAgent;
        this.referer=referer;
    }

    @PrePersist
    public void onCreate(){
        this.clickedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getReferer() {
        return referer;
    }

    public LocalDateTime getClickedAt(){
        return clickedAt;
    }
}
