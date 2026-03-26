package com.hector.encurtadorlink.service;

import com.hector.encurtadorlink.dto.request.CreateUserRequest;
import com.hector.encurtadorlink.dto.response.UserResponseDTO;
import com.hector.encurtadorlink.exception.BusinessException;
import com.hector.encurtadorlink.model.User;
import com.hector.encurtadorlink.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Transactional
@Service
public class UserService {

    private final static Logger logger =
            LoggerFactory.getLogger(UserService.class);

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder){
        this.repository=repository;
        this.passwordEncoder=passwordEncoder;
    }

    public void validateEmail(String email){

        if(email == null || email.isBlank()){
            throw new BusinessException("Email é obrigatório");

        }
        if(repository.existsByEmail(email)){
            throw new BusinessException("Email já cadastrado");
        }

    }


    public UserResponseDTO create(CreateUserRequest dto){
        validateEmail(dto.email());
        User user = new User(
                dto.email(),
                dto.name(),
                passwordEncoder.encode(dto.password()),
                User.Role.USER

        );


        User savedUser = repository.save(user);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getName(),
                savedUser.getRole()
                );
    }



    List<User> listAllUsers(){
        return repository.findAll();
    }





}
