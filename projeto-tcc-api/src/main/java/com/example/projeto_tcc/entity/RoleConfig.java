package com.example.projeto_tcc.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class RoleConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String queue_name;

    private String queue_type;

    private Integer initial_quantity;

    @ElementCollection
    private List<Long> roleIds; // IDs dos Roles que compartilham esse nome
}
