package com.example.projeto_tcc.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class TaskDescriptor extends ProcessElement {

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "task_descriptor_predecessors",
            joinColumns = @JoinColumn(name = "task_descriptor_id"),
            inverseJoinColumns = @JoinColumn(name = "predecessor_id")
    )
    private List<TaskDescriptor> predecessors = new ArrayList<>();
    @Enumerated(EnumType.STRING) // Salva como texto no banco de dados
    @Column(name = "type", nullable = false)
    private ProcessType type;

    public TaskDescriptor() {}

    public TaskDescriptor(int index, ModelInfo modelInfo) {
        super(index, modelInfo);
    }

    @Override
    public boolean optional() {
        return false;
    }

    public List<TaskDescriptor> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(List<TaskDescriptor> predecessors) {
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

