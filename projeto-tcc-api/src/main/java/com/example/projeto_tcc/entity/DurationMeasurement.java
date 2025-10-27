package com.example.projeto_tcc.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
public class DurationMeasurement {
    @Id
    @GeneratedValue
    private int id;
    private String name;
    private double value;

    @ManyToOne
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    private Sample sample;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    private Activity activity;


}
