package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.ObserverMethodElementType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "method_element_observer")
public class MethodElementObserver extends Observer{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private ObserverMethodElementType type;

    @ManyToOne
    @JoinColumn(name = "role_config_id")
    private RoleConfig roleConfig;
}
