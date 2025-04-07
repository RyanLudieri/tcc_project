package com.example.projeto_tcc.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Phase extends ProcessElement {

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "phase_predecessors",
            joinColumns = @JoinColumn(name = "phase_id"),
            inverseJoinColumns = @JoinColumn(name = "predecessor_id")
    )
    private List<Phase> predecessors = new ArrayList<>();
    @Enumerated(EnumType.STRING) // Salva como texto no banco de dados
    @Column(name = "type", nullable = false)
    private ProcessType type;

    public Phase() {}



    public Phase(int index, ModelInfo modelInfo) {
        super(index, modelInfo);
    }

    @Override
    public boolean optional() {
        return true;
    }

    public List<Phase> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(List<Phase> predecessors) {
        this.predecessors = predecessors;
    }

    public ProcessType getType() {
        return type;
    }

    public void setType(ProcessType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

