package com.example.projeto_tcc.repository;

import com.example.projeto_tcc.entity.GeneratorConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeneratorConfigRepository extends JpaRepository<GeneratorConfig, Long> {

    List<GeneratorConfig> findByDeliveryProcessId(Long deliveryProcessId);

    void deleteByDeliveryProcessId(Long deliveryProcessId);
}
