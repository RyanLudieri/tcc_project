package com.example.projeto_tcc.repository;


import com.example.projeto_tcc.entity.Activity;
import com.example.projeto_tcc.entity.MethodElement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MethodElementRepository extends JpaRepository<MethodElement, Long> {
    List<MethodElement> findByParentActivity(Activity parentActivity);
}
