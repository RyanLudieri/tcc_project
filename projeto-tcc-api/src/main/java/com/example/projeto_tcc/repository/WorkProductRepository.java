package com.example.projeto_tcc.repository;

import com.example.projeto_tcc.model.WorkProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkProductRepository extends JpaRepository<WorkProduct, Long> {
}
