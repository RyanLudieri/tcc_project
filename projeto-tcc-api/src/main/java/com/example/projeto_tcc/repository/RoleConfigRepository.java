package com.example.projeto_tcc.repository;

import com.example.projeto_tcc.entity.RoleConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleConfigRepository extends JpaRepository<RoleConfig, Long> {

    List<RoleConfig> findByDeliveryProcessId(Long deliveryProcessId);
}
