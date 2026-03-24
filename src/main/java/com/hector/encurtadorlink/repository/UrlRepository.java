package com.hector.encurtadorlink.repository;

import com.hector.encurtadorlink.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository <Url, Long> {

    Optional<Url> findByShortCode(String shortCode);

}
