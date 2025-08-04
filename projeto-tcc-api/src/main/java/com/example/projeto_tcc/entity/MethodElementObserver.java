package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.ObserverMethodElementType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "method_element_observer")
public class MethodElementObserver extends Observer{

    private ObserverMethodElementType type;

    @ManyToOne
    @JoinColumn(name = "role_config_id")
    private RoleConfig roleConfig;

    @ManyToOne
    @JoinColumn(name = "work_product_config_id")
    private WorkProductConfig workProductConfig;
}
