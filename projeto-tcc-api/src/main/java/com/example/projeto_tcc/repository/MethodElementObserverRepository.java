package com.example.projeto_tcc.repository;

import com.example.projeto_tcc.entity.MethodElementObserver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MethodElementObserverRepository extends JpaRepository<MethodElementObserver, Long> {
}

