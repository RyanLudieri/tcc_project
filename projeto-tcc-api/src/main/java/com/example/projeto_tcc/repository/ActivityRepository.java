package com.example.projeto_tcc.repository;


import com.example.projeto_tcc.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
}