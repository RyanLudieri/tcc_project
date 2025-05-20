package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.entity.MethodType;

public class MethodElementDTO {
    private String name;
    private MethodType type;

    private String modelInfo;
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

    public String getModelInfo() {
        return modelInfo;
    }

    public void setModelInfo(String modelInfo) {
        this.modelInfo = modelInfo;
    }
}
