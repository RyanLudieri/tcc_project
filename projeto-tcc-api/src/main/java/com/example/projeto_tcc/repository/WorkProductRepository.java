package com.example.projeto_tcc.repository;

import com.example.projeto_tcc.entity.WorkProduct;
import com.example.projeto_tcc.enums.WorkProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkProductRepository extends JpaRepository<WorkProduct,Long> {
}
