package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.BestFitDistribution;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Sample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private int size;

    @Enumerated(EnumType.STRING)
    private BestFitDistribution distribution;

    @ManyToOne(cascade = CascadeType.PERSIST) // ou CascadeType.ALL se quiser todas operações cascata
    private DistributionParameter parameter;

    @OneToMany(mappedBy = "sample", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DurationMeasurement> measurements = new ArrayList<>();

    public Sample() {
    }

    public Sample(Integer id, String name, int size, BestFitDistribution distribution, DistributionParameter parameter, List<DurationMeasurement> measurements) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.distribution = distribution;
        this.parameter = parameter;
        this.measurements = measurements;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public BestFitDistribution getDistribution() {
        return distribution;
    }

    public void setDistribution(BestFitDistribution distribution) {
        this.distribution = distribution;
    }

    public DistributionParameter getParameter() {
        return parameter;
    }

    public void setParameter(DistributionParameter parameter) {
        this.parameter = parameter;
    }

    public List<DurationMeasurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<DurationMeasurement> measurements) {
        this.measurements = measurements;
    }
}
