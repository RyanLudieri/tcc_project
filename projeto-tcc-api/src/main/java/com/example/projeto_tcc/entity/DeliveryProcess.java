package com.example.projeto_tcc.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class DeliveryProcess extends Process {
    @Override
    public boolean optional() {
        return false;
    }
}


