package com.example.projeto_tcc.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Process extends Activity {

    @OneToOne(cascade = CascadeType.ALL)
    private WorkBreakdownStructure wbs;

    @Override
    public boolean optional() {
        return false;
    }
}
