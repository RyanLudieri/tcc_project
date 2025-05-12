package com.example.projeto_tcc.entity;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class ProcessElement extends Activity {

    @Override
    public boolean optional() {
        return false;
    }
}
