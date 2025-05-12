package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.entity.MethodType;
import com.example.projeto_tcc.entity.ModelInfo;

public class MethodElementDTO {
    private String name;
    private MethodType type;

    private ModelInfo modelInfo;
    private Integer parentIndex; // índice da Activity pai, se houver

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MethodType getType() {
        return type;
    }

    public void setType(MethodType type) {
        this.type = type;
    }

    public Integer getParentIndex() {
        return parentIndex;
    }

    public void setParentIndex(Integer parentIndex) {
        this.parentIndex = parentIndex;
    }

    public ModelInfo getModelInfo() {
        return modelInfo;
    }

    public void setModelInfo(ModelInfo modelInfo) {
        this.modelInfo = modelInfo;
    }
}
