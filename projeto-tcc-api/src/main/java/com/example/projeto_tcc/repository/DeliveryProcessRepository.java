package com.example.projeto_tcc.repository;

import com.example.projeto_tcc.entity.DeliveryProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryProcessRepository extends JpaRepository<DeliveryProcess, Long> {
}

