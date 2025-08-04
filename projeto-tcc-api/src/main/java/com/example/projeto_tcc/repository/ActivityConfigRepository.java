package com.example.projeto_tcc.repository;

import com.example.projeto_tcc.entity.ActivityConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityConfigRepository extends JpaRepository<ActivityConfig, Long> {
}

