package com.example.projeto_tcc.repository;

import com.example.projeto_tcc.entity.IterationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IterationConfigRepository extends JpaRepository<IterationConfig, Long> {

    @Transactional
    void deleteByDeliveryProcessId(Long deliveryProcessId);

    List<IterationConfig> findByDeliveryProcessId(Long deliveryProcessId);
}
