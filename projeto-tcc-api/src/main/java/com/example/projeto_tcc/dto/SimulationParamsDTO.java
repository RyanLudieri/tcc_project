package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.enums.ConditionToProcess;
import com.example.projeto_tcc.enums.DependencyType;
import com.example.projeto_tcc.enums.IterationBehavior;
import com.example.projeto_tcc.enums.ProcessingQuantity;

import java.util.List;

public class SimulationParamsDTO {
    private Long activityId;
    private Integer sampleId;
    private List<Integer> observerIds;
    private DependencyType dependencyType;
    private ConditionToProcess conditionToProcess;
    private ProcessingQuantity processingQuantity;
    private IterationBehavior iterationBehavior;
    private int requiredResources;

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

    public List<Integer> getObserverIds() {
        return observerIds;
    }

    public void setObserverIds(List<Integer> observerIds) {
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
}
