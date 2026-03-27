package com.hector.encurtadorlink.service;

import com.hector.encurtadorlink.dto.response.UserResponseDTO;
import com.hector.encurtadorlink.model.User;
import com.hector.encurtadorlink.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository repository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService){
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String login(String email, String password){

        User user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Invalid credentials");
        }

        return jwtService.generateToken(user);
    }
}

