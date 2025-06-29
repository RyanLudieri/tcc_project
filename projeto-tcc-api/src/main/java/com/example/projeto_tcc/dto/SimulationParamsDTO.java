package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.enums.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class SimulationParamsDTO {
    private Long activityId;
    private Integer sampleId;
    private List<Long> observerIds;
    private DependencyType dependencyType;
    private ConditionToProcess conditionToProcess;
    private ProcessingQuantity processingQuantity;
    private IterationBehavior iterationBehavior;
    private int requiredResources;

    private int timeBox;

    private TimeScale timeScale;
}
