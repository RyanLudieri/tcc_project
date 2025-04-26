package com.example.projeto_tcc.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class ProcessElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String briefDescription;
    private double completeness;

    @ManyToOne
    private ProcessElement superActivity; // Pai

    @OneToMany(mappedBy = "superActivity", cascade = CascadeType.ALL)
    private List<ProcessElement> children; // Filhos

    @ManyToMany
    private List<ProcessElement> predecessors; // Predecessores

    public ProcessElement() {
    }

    public ProcessElement(Long id, String name, String briefDescription, double completeness, ProcessElement superActivity, List<ProcessElement> children, List<ProcessElement> predecessors) {
        this.id = id;
        this.name = name;
        this.briefDescription = briefDescription;
        this.completeness = completeness;
        this.superActivity = superActivity;
        this.children = children;
        this.predecessors = predecessors;
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

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public double getCompleteness() {
        return completeness;
    }

    public void setCompleteness(double completeness) {
        this.completeness = completeness;
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

