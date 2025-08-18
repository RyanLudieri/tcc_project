package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.enums.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ActivityConfigDTO {
    private Long activityId;
    private String name;
    private String type;
    private Long parentId;

    private DependencyType dependencyType;
    private int timeBox;
    private ConditionToProcess conditionToProcess;
    private ProcessingQuantity processingQuantity;
    private IterationBehavior iterationBehavior;
    private int requiredResources;

    private BestFitDistribution distributionType;
    private DistributionParameterDTO distributionParameter;

    private List<ActivityObserverDTO> observers;
}

