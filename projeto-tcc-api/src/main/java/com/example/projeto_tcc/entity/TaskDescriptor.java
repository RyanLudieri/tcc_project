package com.example.projeto_tcc.entity;

import jakarta.persistence.Entity;

@Entity
public class TaskDescriptor extends Activity{
    @Override
    public boolean optional() {
        return false;
    }
}
