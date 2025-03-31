package com.example.projeto_tcc.repository;

import com.example.projeto_tcc.model.TaskDescriptor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskDescriptorRepository extends JpaRepository<TaskDescriptor, Long> {
}

