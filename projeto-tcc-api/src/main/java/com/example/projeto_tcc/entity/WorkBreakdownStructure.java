package com.example.projeto_tcc.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class WorkBreakdownStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Activity> processElements;

    @OneToMany(cascade = CascadeType.ALL)
    private List<MethodElement> methodElements;
}

