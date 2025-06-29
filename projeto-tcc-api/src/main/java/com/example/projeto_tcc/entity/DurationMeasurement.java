package com.example.projeto_tcc.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
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

}
