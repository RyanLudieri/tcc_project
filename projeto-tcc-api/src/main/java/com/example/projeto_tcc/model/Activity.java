package com.example.projeto_tcc.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Activity extends ProcessElement {

    @ManyToMany
    @JoinTable(
            name = "activity_predecessors",
            joinColumns = @JoinColumn(name = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "predecessor_id")
    )
    private List<Activity> predecessors = new ArrayList<>();
    @Enumerated(EnumType.STRING) // Salva como texto no banco de dados
    @Column(name = "type", nullable = false)
    private ProcessType type;

    public Activity() {}

    public Activity(int index, ModelInfo modelInfo) {
        super(index, modelInfo);
    }

    @Override
    public boolean optional() {
        return true;
    }

    public List<Activity> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(List<Activity> predecessors) {
        this.predecessors = predecessors;
    }

    public ProcessType getType() {
        return type;
    }

    public void setType(ProcessType type) {
        this.type = type;
    }
}
