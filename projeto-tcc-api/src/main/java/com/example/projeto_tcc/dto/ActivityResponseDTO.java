package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.enums.*;

import java.util.List;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProcessType getType() {
        return type;
    }

    public void setType(ProcessType type) {
        this.type = type;
    }

    public Integer getRequiredResources() {
        return requiredResources;
    }

    public void setRequiredResources(Integer requiredResources) {
        this.requiredResources = requiredResources;
    }

    public TimeScale getTimeScale() {
        return timeScale;
    }

    public void setTimeScale(TimeScale timeScale) {
        this.timeScale = timeScale;
    }

    public DependencyType getDependencyType() {
        return dependencyType;
    }

    public void setDependencyType(DependencyType dependencyType) {
        this.dependencyType = dependencyType;
    }

    public ConditionToProcess getConditionToProcess() {
        return conditionToProcess;
    }

    public void setConditionToProcess(ConditionToProcess conditionToProcess) {
        this.conditionToProcess = conditionToProcess;
    }

    public ProcessingQuantity getProcessingQuantity() {
        return processingQuantity;
    }

    public void setProcessingQuantity(ProcessingQuantity processingQuantity) {
        this.processingQuantity = processingQuantity;
    }

    public IterationBehavior getIterationBehavior() {
        return iterationBehavior;
    }

    public void setIterationBehavior(IterationBehavior iterationBehavior) {
        this.iterationBehavior = iterationBehavior;
    }

    public List<Long> getObserverIds() {
        return observerIds;
    }

    public void setObserverIds(List<Long> observerIds) {
        this.observerIds = observerIds;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public int getTimeBox() {
        return timeBox;
    }

    public void setTimeBox(int timeBox) {
        this.timeBox = timeBox;
    }
}
