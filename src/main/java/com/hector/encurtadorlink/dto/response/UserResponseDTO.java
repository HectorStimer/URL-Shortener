package com.hector.encurtadorlink.dto.response;

import com.hector.encurtadorlink.model.User.Role;


public record UserResponseDTO (Long id, String email, String name, Role role){
}
