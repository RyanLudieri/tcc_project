package com.example.projeto_tcc.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

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
    private Double standardDeviation;

    // UNIFORM
    private Double low;
    private Double high;

    // LOGNORMAL, WEIBULL, GAMMA
    private Double scale;
    private Double shape;

}

