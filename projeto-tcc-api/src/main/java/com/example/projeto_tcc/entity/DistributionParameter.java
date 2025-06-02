package com.example.projeto_tcc.entity;

import jakarta.persistence.*;

@Entity
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

    // Getters e setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getMean() {
        return mean;
    }

    public void setMean(Double mean) {
        this.mean = mean;
    }

    public Double getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(Double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public Double getLambda() {
        return lambda;
    }

    public void setLambda(Double lambda) {
        this.lambda = lambda;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getShape() {
        return shape;
    }

    public void setShape(Double shape) {
        this.shape = shape;
    }

    public Double getScale() {
        return scale;
    }

    public void setScale(Double scale) {
        this.scale = scale;
    }

    public Double getAlpha() {
        return alpha;
    }

    public void setAlpha(Double alpha) {
        this.alpha = alpha;
    }

    public Double getBeta() {
        return beta;
    }

    public void setBeta(Double beta) {
        this.beta = beta;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }
}
