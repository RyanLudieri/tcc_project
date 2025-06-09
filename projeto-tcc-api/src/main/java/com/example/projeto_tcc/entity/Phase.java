package com.example.projeto_tcc.entity;

import jakarta.persistence.Entity;

@Entity
public class Phase extends Activity{
    @Override
    public boolean optional() {
        return true;
    }
}
