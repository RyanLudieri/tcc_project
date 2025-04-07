package com.example.projeto_tcc.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Milestone extends ProcessElement {

    @ManyToMany
    @JoinTable(
            name = "milestone_predecessors",
            joinColumns = @JoinColumn(name = "milestone_id"),
            inverseJoinColumns = @JoinColumn(name = "predecessor_id")
    )
    private List<Milestone> predecessors = new ArrayList<>();
    @Enumerated(EnumType.STRING) // Salva como texto no banco de dados
    @Column(name = "type", nullable = false)
    private ProcessType type;

    public Milestone() {}

    public Milestone(int index, ModelInfo modelInfo) {
        super(index, modelInfo);
    }


    @Override
    public boolean optional() {
        return true;
    }

    public List<Milestone> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(List<Milestone> predecessors) {
        this.predecessors = predecessors;
    }

    public ProcessType getType() {
        return type;
    }

    public void setType(ProcessType type) {
        this.type = type;
    }
}

