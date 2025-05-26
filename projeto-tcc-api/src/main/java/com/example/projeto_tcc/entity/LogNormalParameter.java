package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.BestFitDistribution;
import jakarta.persistence.Entity;

@Entity
public class LogNormalParameter extends DistributionParameter {
    private double scale;
    private double shape;

    public LogNormalParameter() {
    }

    public LogNormalParameter(Long id, BestFitDistribution distribution, double scale, double shape) {
        super(id, distribution);
        this.scale = scale;
        this.shape = shape;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getShape() {
        return shape;
    }

    public void setShape(double shape) {
        this.shape = shape;
    }
}
