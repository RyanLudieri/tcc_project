package com.example.projeto_tcc.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class GlobalResourceStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String resourceName;
    private double averageUtilization;
    private double stdDevUtilization;

    @ManyToOne
    @JoinColumn(name = "global_result_id")
    private GlobalSimulationResult globalResult;
}
