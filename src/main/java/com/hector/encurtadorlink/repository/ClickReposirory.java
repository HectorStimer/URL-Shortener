package com.hector.encurtadorlink.repository;

import com.hector.encurtadorlink.model.Click;
import com.hector.encurtadorlink.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClickReposirory extends JpaRepository <Click, Long> {

    List<Click> findByUrl(Url url);

}
