package com.example.projeto_tcc.repository;

import com.example.projeto_tcc.entity.DeliveryProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeliveryProcessRepository extends JpaRepository<DeliveryProcess, Long> {
    @Query("SELECT dp FROM DeliveryProcess dp " +
            "LEFT JOIN FETCH dp.generatorConfigs gc " +
            "LEFT JOIN FETCH gc.distribution " +
            "LEFT JOIN FETCH gc.targetWorkProduct " +
            "WHERE dp.id = :processId")
    Optional<DeliveryProcess> findProcessWithAllConfigsById(@Param("processId") Long processId);
}

