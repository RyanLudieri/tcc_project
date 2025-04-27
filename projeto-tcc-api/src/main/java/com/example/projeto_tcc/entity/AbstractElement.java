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

    protected int index;

    @ManyToMany
    protected List<ProcessElement> predecessors = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    protected ModelInfo modelInfo;

    @Enumerated(EnumType.STRING)
    protected ProcessType type;

    public abstract boolean optional();

    public AbstractElement() {
    }

    public AbstractElement(Long id, int index, List<ProcessElement> predecessors, ModelInfo modelInfo, ProcessType type) {
        this.id = id;
        this.index = index;
        this.predecessors = predecessors;
        this.modelInfo = modelInfo;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<ProcessElement> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(List<ProcessElement> predecessors) {
        this.predecessors = predecessors;
    }

    public ModelInfo getModelInfo() {
        return modelInfo;
    }

    public void setModelInfo(ModelInfo modelInfo) {
        this.modelInfo = modelInfo;
    }

    public ProcessType getType() {
        return type;
    }

    public void setType(ProcessType type) {
        this.type = type;
    }
}
