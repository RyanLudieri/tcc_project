package com.example.projeto_tcc.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Iteration extends ProcessElement {

    @ManyToMany
    @JoinTable(
            name = "iteration_predecessors",
            joinColumns = @JoinColumn(name = "iteration_id"),
            inverseJoinColumns = @JoinColumn(name = "predecessor_id")
    )
    private List<Iteration> predecessors = new ArrayList<>();
    @Enumerated(EnumType.STRING) // Salva como texto no banco de dados
    @Column(name = "type", nullable = false)
    private ProcessType type;

    public Iteration() {}

    public Iteration(int index, ModelInfo modelInfo) {
        super(index, modelInfo);
    }

    @Override
    public boolean optional() {
        return false;
    }

    public List<Iteration> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(List<Iteration> predecessors) {
        this.predecessors = predecessors;
    }

    public ProcessType getType() {
        return type;
    }

    public void setType(ProcessType type) {
        this.type = type;
    }
}

