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

    // NORMAL, EXPONENTIAL, POISSON
    private Double mean;
    private Double standardDeviation; // para NORMAL

    // UNIFORM
    private Double min;
    private Double max;

    // LOGNORMAL
    private Double scale;  // também usado por GAMMA e WEIBULL
    private Double shape;  // também usado por GAMMA

    // GEOMETRIC
    private Double probability;

    // POISSON (em alguns casos chamado de lambda)
    private Double lambda;

    // WEIBULL
    private Double alpha;  // shape
    private Double beta;   // scale

}

