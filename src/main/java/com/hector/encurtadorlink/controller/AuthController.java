package com.hector.encurtadorlink.controller;

import com.hector.encurtadorlink.dto.response.AuthResponse;
import com.hector.encurtadorlink.dto.request.CreateUserRequest;
import com.hector.encurtadorlink.dto.request.LoginRequest;
import com.hector.encurtadorlink.dto.response.UserResponseDTO;
import com.hector.encurtadorlink.model.User;
import com.hector.encurtadorlink.service.AuthService;
import com.hector.encurtadorlink.service.JwtService;
import com.hector.encurtadorlink.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(AuthService authService,
                          UserService userService,
                          JwtService jwtService) {
        this.authService = authService;
        this.userService = userService;
        this.jwtService = jwtService;
    }


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody CreateUserRequest request){

        UserResponseDTO user = userService.create(request);


        User createdUser = new User(
                user.email(),
                user.name(),
                "", // senha não vem no DTO, mas não usamos aqui
                user.role()
        );

        String token = jwtService.generateToken(createdUser);

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){

        String token = authService.login(request.email(), request.password());

        return ResponseEntity.ok(new AuthResponse(token));
    }
}