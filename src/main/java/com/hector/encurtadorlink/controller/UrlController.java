package com.hector.encurtadorlink.controller;


import com.hector.encurtadorlink.dto.request.CreateUrlRequest;
import com.hector.encurtadorlink.dto.response.UrlResponse;
import com.hector.encurtadorlink.model.Url;
import com.hector.encurtadorlink.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/url/")
public class UrlController {

    private final UrlService service;

    public UrlController(UrlService service){
        this.service=service;
    }


    @PostMapping
    public ResponseEntity<UrlResponse> saveUrl(@RequestBody @Valid CreateUrlRequest dto){

        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));

    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> getShortCode(@PathVariable String shortCode){
        Url url = service.findByShortCode(shortCode);
        URI location = URI.create(url.getOriginalUrl());
        return ResponseEntity.status(HttpStatus.FOUND).location(location).build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUrl(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


}
