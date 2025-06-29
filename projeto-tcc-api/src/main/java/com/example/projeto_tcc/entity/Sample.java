package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.BestFitDistribution;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
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
}
