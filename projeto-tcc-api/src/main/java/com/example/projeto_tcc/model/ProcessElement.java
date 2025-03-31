package com.example.projeto_tcc.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
public abstract class ProcessElement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int index;

    @Enumerated(EnumType.STRING)
    private ModelInfo modelInfo;

    public ProcessElement() {}

    public ProcessElement(int index, ModelInfo modelInfo) {
        this.index = index;
        this.modelInfo = modelInfo;
    }

    public abstract boolean optional();

    // Getters e Setters
    public Long getId() { return id; }
    public int getIndex() { return index; }
    public ModelInfo getModelInfo() { return modelInfo; }

    public void setIndex(int index) { this.index = index; }
    public void setModelInfo(ModelInfo modelInfo) { this.modelInfo = modelInfo; }
}
