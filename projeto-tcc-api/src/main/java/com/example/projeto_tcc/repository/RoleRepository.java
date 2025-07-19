package com.example.projeto_tcc.repository;

import com.example.projeto_tcc.entity.Activity;
import com.example.projeto_tcc.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findAllByParentActivityIn(List<Activity> activities);
}
