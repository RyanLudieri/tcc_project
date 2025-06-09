package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.enums.*;

import java.util.List;

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

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Integer getSampleId() {
        return sampleId;
    }

    public void setSampleId(Integer sampleId) {
        this.sampleId = sampleId;
    }

    public List<Long> getObserverIds() {
        return observerIds;
    }

    public void setObserverIds(List<Long> observerIds) {
        this.observerIds = observerIds;
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

    public int getRequiredResources() {
        return requiredResources;
    }

    public void setRequiredResources(int requiredResources) {
        this.requiredResources = requiredResources;
    }

    public TimeScale getTimeScale() {
        return timeScale;
    }

    public void setTimeScale(TimeScale timeScale) {
        this.timeScale = timeScale;
    }

    public int getTimeBox() {
        return timeBox;
    }

    public void setTimeBox(int timeBox) {
        this.timeBox = timeBox;
    }
}
