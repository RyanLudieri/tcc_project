package com.example.projeto_tcc.repository;

import com.example.projeto_tcc.entity.RoleConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleConfigRepository extends JpaRepository<RoleConfig, Long> {

    List<RoleConfig> findByDeliveryProcessId(Long deliveryProcessId);

    List<RoleConfig> findByActivities_Id(Long activityId);

    void deleteByDeliveryProcessId(Long deliveryProcessId);

}
