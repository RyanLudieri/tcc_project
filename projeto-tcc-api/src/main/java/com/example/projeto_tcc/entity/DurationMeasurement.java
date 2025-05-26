package com.example.projeto_tcc.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class DurationMeasurement {
    @Id
    private String id;
    private String name;
    private double duration;

    @ManyToOne
    private Sample sample;

    public DurationMeasurement() {
    }

    public DurationMeasurement(String id, String name, double duration, Sample sample) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.sample = sample;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }
}
