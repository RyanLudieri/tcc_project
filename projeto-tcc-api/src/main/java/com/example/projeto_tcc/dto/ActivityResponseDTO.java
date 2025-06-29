package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.enums.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ActivityResponseDTO {
    private Long id;
    private String name;
    private ProcessType type;
    private Integer requiredResources;

    private int timeBox;
    private TimeScale timeScale;
    private DependencyType dependencyType;
    private ConditionToProcess conditionToProcess;
    private ProcessingQuantity processingQuantity;
    private IterationBehavior iterationBehavior;
    private List<Long> observerIds;
    private Integer sampleId;

    public ActivityResponseDTO() {}

    public ActivityResponseDTO(Long id, String name, ProcessType type, Integer requiredResources, int timeBox, TimeScale timeScale, DependencyType dependencyType, ConditionToProcess conditionToProcess, ProcessingQuantity processingQuantity, IterationBehavior iterationBehavior, List<Long> observerIds, Integer sampleId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.requiredResources = requiredResources;
        this.timeBox = timeBox;
        this.timeScale = timeScale;
        this.dependencyType = dependencyType;
        this.conditionToProcess = conditionToProcess;
        this.processingQuantity = processingQuantity;
        this.iterationBehavior = iterationBehavior;
        this.observerIds = observerIds;
        this.sampleId = sampleId;
    }

}
