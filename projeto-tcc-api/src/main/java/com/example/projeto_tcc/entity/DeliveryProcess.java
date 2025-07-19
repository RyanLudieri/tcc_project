package com.example.projeto_tcc.entity;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class DeliveryProcess extends Process {
    @Override
    public boolean optional() {
        return false;
    }
}


