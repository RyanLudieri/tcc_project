package com.example.projeto_tcc.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class GlobalQueueStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String queueName;
    private String taskName;
    private Double averageCount;
    private Double stdDevCount; // O desvio padrão que faltava

    @ManyToOne
    @JoinColumn(name = "global_result_id")
    private GlobalSimulationResult globalResult;
}
