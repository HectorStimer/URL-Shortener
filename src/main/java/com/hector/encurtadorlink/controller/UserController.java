package com.hector.encurtadorlink.controller;


import com.hector.encurtadorlink.dto.request.CreateUserRequest;
import com.hector.encurtadorlink.dto.response.UserResponseDTO;
import com.hector.encurtadorlink.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/")
public class UserController {
    private final UserService service;

    public UserController(UserService service){
        this.service=service;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@RequestBody CreateUserRequest dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }


}
