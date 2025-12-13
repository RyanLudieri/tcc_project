package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.BestFitDistribution;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Sample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private int size;

    @Enumerated(EnumType.STRING)
    private BestFitDistribution distribution;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private DistributionParameter parameter;

    @OneToMany(mappedBy = "sample", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DurationMeasurement> measurements = new ArrayList<>();


}
