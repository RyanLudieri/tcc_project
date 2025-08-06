package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.*;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class ActivityConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Activity activity;

    @OneToMany(mappedBy = "activityConfig", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActivityObserver> observers = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    private Sample sample;

    @Enumerated(EnumType.STRING)
    private BestFitDistribution distributionType;

    @OneToOne(cascade = CascadeType.ALL)
    private DistributionParameter distributionParameter;

    @Enumerated(EnumType.STRING)
    private DependencyType dependencyType;

    private int timeBox;

    @Enumerated(EnumType.STRING)
    private ConditionToProcess conditionToProcess;

    @Enumerated(EnumType.STRING)
    private ProcessingQuantity processingQuantity;

    @Enumerated(EnumType.STRING)
    private IterationBehavior iterationBehavior;

    private int requiredResources;


    // Getters e Setters
}


