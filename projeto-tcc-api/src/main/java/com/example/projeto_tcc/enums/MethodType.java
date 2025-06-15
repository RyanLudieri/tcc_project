package com.example.projeto_tcc.enums;

import com.example.projeto_tcc.entity.MethodElement;
import com.example.projeto_tcc.entity.Role;
import com.example.projeto_tcc.entity.WorkProduct;

public enum MethodType {
    WORKPRODUCT,
    ROLE;

    public MethodElement createInstance() {
        return switch (this) {
            case WORKPRODUCT -> new WorkProduct();
            case ROLE -> new Role();
        };
    }
}

