package com.example.projeto_tcc.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class ProcessElement extends AbstractElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JsonBackReference
    private ProcessElement superActivity; // Pai

    @OneToMany(mappedBy = "superActivity", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ProcessElement> children; // Filhos

    @ManyToMany
    private List<ProcessElement> predecessors; // Predecessores

    public ProcessElement() {
    }

    public ProcessElement(Long id, int index, List<ProcessElement> predecessors, ModelInfo modelInfo, ProcessType type, Long id1, String name, ProcessElement superActivity, List<ProcessElement> children, List<ProcessElement> predecessors1) {
        super(id, index, predecessors, modelInfo, type);
        this.id = id1;
        this.name = name;
        this.superActivity = superActivity;
        this.children = children;
        this.predecessors = predecessors1;
    }

    @Override
    public boolean optional() {
        return false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProcessElement getSuperActivity() {
        return superActivity;
    }

    public void setSuperActivity(ProcessElement superActivity) {
        this.superActivity = superActivity;
    }

    public List<ProcessElement> getChildren() {
        return children;
    }

    public void setChildren(List<ProcessElement> children) {
        this.children = children;
    }

    public List<ProcessElement> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(List<ProcessElement> predecessors) {
        this.predecessors = predecessors;
    }
}

