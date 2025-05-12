package com.example.projeto_tcc.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.util.List;

public class MethodElement extends AbstractElement{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter
    private String name;

    @Override
    public boolean optional() {
        return false;
    }

    public MethodElement() {
    }

    public MethodElement(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public MethodElement(Long id, Integer index, List<ProcessElement> predecessors, ModelInfo modelInfo, ProcessType type, Long id1, String name) {
        super(id, index, predecessors, modelInfo, type);
        this.id = id1;
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
