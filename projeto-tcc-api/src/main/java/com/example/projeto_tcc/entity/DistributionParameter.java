package com.example.projeto_tcc.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
public class DistributionParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CONSTANT
    private Double constant;

    // EXPONENTIAL
    private Double mean;

    // NORMAL, NEGATIVE_EXPONENTIAL, POISSON
    private Double average;

    // NORMAL
    private Double standardDeviation; // para NORMAL

    // UNIFORM
    private Double low;
    private Double high;

    // LOGNORMAL, WEIBULL, GAMMA
    private Double scale;
    private Double shape;

}

