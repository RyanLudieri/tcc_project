package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.ObserverMethodElementType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "method_element_observer")
public class MethodElementObserver extends Observer {

    @Enumerated(EnumType.STRING)
    private ObserverMethodElementType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_config_id")
    @JsonBackReference
    private RoleConfig roleConfig;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_product_config_id")
    @JsonBackReference
    private WorkProductConfig workProductConfig;
}

