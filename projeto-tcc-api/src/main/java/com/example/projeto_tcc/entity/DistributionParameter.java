package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.BestFitDistribution;
import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DistributionParameter {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private BestFitDistribution distribution;

    public DistributionParameter() {
    }

    public DistributionParameter(Long id, BestFitDistribution distribution) {
        this.id = id;
        this.distribution = distribution;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BestFitDistribution getDistribution() {
        return distribution;
    }

    public void setDistribution(BestFitDistribution distribution) {
        this.distribution = distribution;
    }
}