package com.example.projeto_tcc.entity;

import jakarta.persistence.Entity;

@Entity
public class WorkProduct extends MethodElement{
    @Override
    public boolean optional() {
        return false;
    }
}
