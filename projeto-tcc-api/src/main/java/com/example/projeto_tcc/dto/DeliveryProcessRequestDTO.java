package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.model.ModelInfo;
import com.example.projeto_tcc.model.ProcessType;

public class DeliveryProcessRequestDTO {
    private String name;
    private Integer index;
    private ModelInfo modelInfo;
    private ProcessType processType;
    private Long parentId; // pode ser nulo

    // Getters e Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getIndex() { return index; }
    public void setIndex(Integer index) { this.index = index; }

    public ModelInfo getModelInfo() { return modelInfo; }
    public void setModelInfo(ModelInfo modelInfo) { this.modelInfo = modelInfo; }

    public ProcessType getProcessType() { return processType; }
    public void setProcessType(ProcessType processType) { this.processType = processType; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }


}

