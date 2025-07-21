package com.example.projeto_tcc.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Process extends Activity {

    @OneToOne(cascade = CascadeType.ALL)
    private WorkBreakdownStructure wbs;


}
