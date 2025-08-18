package com.example.projeto_tcc.repository;

import com.example.projeto_tcc.entity.ActivityObserver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityObserverRepository extends JpaRepository<ActivityObserver, Long> {}

