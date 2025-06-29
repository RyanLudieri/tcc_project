package com.example.projeto_tcc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DistributionParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double mean;
    private Double standardDeviation;
    private Double lambda;
    private Double min;
    private Double max;
    private Double shape;
    private Double scale;
    private Double alpha;
    private Double beta;
    private Double probability;

    public DistributionParameter() {
    }

    public DistributionParameter(Integer id, Double mean, Double standardDeviation, Double lambda, Double min, Double max, Double shape, Double scale, Double alpha, Double beta, Double probability) {
        this.id = id;
        this.mean = mean;
        this.standardDeviation = standardDeviation;
        this.lambda = lambda;
        this.min = min;
        this.max = max;
        this.shape = shape;
        this.scale = scale;
        this.alpha = alpha;
        this.beta = beta;
        this.probability = probability;
    }

}
