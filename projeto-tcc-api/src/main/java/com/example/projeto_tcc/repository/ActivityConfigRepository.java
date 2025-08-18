package com.example.projeto_tcc.repository;

import com.example.projeto_tcc.entity.ActivityConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityConfigRepository extends JpaRepository<ActivityConfig, Long> {

    Optional<ActivityConfig> findByActivityId(Long activityId);

}


