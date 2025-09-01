package com.example.projeto_tcc.repository;

import com.example.projeto_tcc.entity.XACDMLFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface XACDMLFileRepository extends JpaRepository<XACDMLFile, Long> {
}
