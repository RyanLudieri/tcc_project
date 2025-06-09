package com.example.projeto_tcc.entity;

import jakarta.persistence.Entity;

@Entity
public class Iteration extends Activity{

    @Override
    public boolean optional() {
        return false;
    }
}
