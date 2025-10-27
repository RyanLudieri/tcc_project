package com.example.projeto_tcc.repository;

import com.example.projeto_tcc.entity.Activity;
import com.example.projeto_tcc.entity.DeliveryProcess;
import com.example.projeto_tcc.entity.RoleConfig;
import com.example.projeto_tcc.entity.WorkProductConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkProductConfigRepository extends JpaRepository<WorkProductConfig, Long> {

    List<WorkProductConfig> findByActivity_Id(Long activityId);

    List<WorkProductConfig> findByDeliveryProcessId(Long deliveryProcessId);

    Optional<WorkProductConfig> findByDeliveryProcessAndIsDestroyerIsTrue(DeliveryProcess process);

    void deleteByDeliveryProcessId(Long deliveryProcessId);

}

