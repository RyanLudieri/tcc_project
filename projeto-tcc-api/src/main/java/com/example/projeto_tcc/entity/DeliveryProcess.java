package com.example.projeto_tcc.entity;

import jakarta.persistence.Entity;

@Entity
public class DeliveryProcess extends Process {
    @Override
    public boolean optional() {
        return false;
    }
}


