package com.hector.encurtadorlink.controller;

import com.hector.encurtadorlink.dto.response.StatsResponse;
import com.hector.encurtadorlink.model.Click;
import com.hector.encurtadorlink.model.Url;
import com.hector.encurtadorlink.service.ClickService;
import com.hector.encurtadorlink.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stats/")
public class StatsController {
    private final ClickService service;
    private final UrlService urlService;

    public StatsController(ClickService service, UrlService urlService){
        this.service=service;
        this.urlService=urlService;
    }


    @GetMapping("/{shortCode}/")
    public ResponseEntity<StatsResponse> statisticsURL(@PathVariable  String shortCode){
        Url url = urlService.findByShortCode(shortCode);
        return ResponseEntity.ok(service.getStats(url));
    }
}
