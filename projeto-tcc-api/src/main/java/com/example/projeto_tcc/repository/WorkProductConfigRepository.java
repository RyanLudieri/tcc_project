package com.example.projeto_tcc.repository;

import com.example.projeto_tcc.entity.Activity;
import com.example.projeto_tcc.entity.WorkProductConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkProductConfigRepository extends JpaRepository<WorkProductConfig, Long> {
    List<WorkProductConfig> findByActivity(Activity activity);
}

