package com.hector.encurtadorlink.repository;

import com.hector.encurtadorlink.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository <User, Long>{

    boolean existsByEmail(String email);


}
