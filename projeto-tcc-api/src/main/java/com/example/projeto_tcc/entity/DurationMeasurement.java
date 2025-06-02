package com.example.projeto_tcc.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class DurationMeasurement {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    private double value;

    @ManyToOne
    @JsonBackReference
    private Sample sample;

    @ManyToOne
    private Activity activity;


    public DurationMeasurement() {
    }

    public DurationMeasurement(int id, String name, double value, Sample sample) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.sample = sample;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
