package com.example.projeto_tcc.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
@Data
public abstract class AbstractElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    protected Integer index;


    protected String modelInfo;

    @Enumerated(EnumType.STRING)
    protected ProcessType type;

    public abstract boolean optional();

    public AbstractElement() {
    }

    public AbstractElement(Long id, Integer index, String modelInfo, ProcessType type) {
        this.id = id;
        this.index = index;
        this.modelInfo = modelInfo;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getModelInfo() {
        return modelInfo;
    }

    public void setModelInfo(String modelInfo) {
        this.modelInfo = modelInfo;
    }

    public ProcessType getType() {
        return type;
    }

    public void setType(ProcessType type) {
        this.type = type;
    }
}
