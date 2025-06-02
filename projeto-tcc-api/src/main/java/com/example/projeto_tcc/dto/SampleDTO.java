package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.entity.DistributionParameter;
import com.example.projeto_tcc.enums.BestFitDistribution;

import java.util.List;

public class SampleDTO {
    private Integer id;
    private String name;
    private int size;
    private BestFitDistribution distribution;
    private DistributionParameter parameter;
    private List<DurationMeasurementDTO> measurements;

    public SampleDTO() {}

    public SampleDTO(Integer id, String name, int size, BestFitDistribution distribution,
                     DistributionParameter parameter, List<DurationMeasurementDTO> measurements) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.distribution = distribution;
        this.parameter = parameter;
        this.measurements = measurements;
    }

    // Getters e Setters
    // ...


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

    public List<DurationMeasurementDTO> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<DurationMeasurementDTO> measurements) {
        this.measurements = measurements;
    }
}

