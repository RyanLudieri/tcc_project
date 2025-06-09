package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.*;
import com.example.projeto_tcc.serializer.CustomElementSerializer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class Activity extends AbstractElement {

    private String name;

    @ManyToOne
    @JsonBackReference
    private Activity superActivity;

    @OneToMany(mappedBy = "superActivity", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Activity> children;

    @ManyToMany
    @JoinTable(
            name = "activity_predecessors",
            joinColumns = @JoinColumn(name = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "predecessor_id")
    )
    @JsonSerialize(using = CustomElementSerializer.class)
    private List<Activity> predecessors;


    // --------------------------
    // CAMPOS DE SIMULAÇÃO AQUI
    // --------------------------

    @OneToOne(cascade = CascadeType.ALL)
    private Sample sample;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL)
    private List<Observer> observers;

    @Enumerated(EnumType.STRING)
    private DependencyType dependencyType;

    @Enumerated(EnumType.STRING)
    private ConditionToProcess conditionToProcess;

    @Enumerated(EnumType.STRING)
    private ProcessingQuantity processingQuantity;

    @Enumerated(EnumType.STRING)
    private IterationBehavior iterationBehavior;

    private int requiredResources;

    private int timeBox;

    @Getter
    private TimeScale timeScale;

    // --------------------------

    @Override
    public boolean optional() {
        return false;
    }

    public Activity() {
    }

    public Activity(Long id, Integer index, String modelInfo, ProcessType type, String name, Activity superActivity, List<Activity> children, List<Activity> predecessors) {
        super(id, index, modelInfo, type);
        this.name = name;
        this.superActivity = superActivity;
        this.children = children;
        this.predecessors = predecessors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Activity getSuperActivity() {
        return superActivity;
    }

    public void setSuperActivity(Activity superActivity) {
        this.superActivity = superActivity;
    }

    public List<Activity> getChildren() {
        return children;
    }

    public void setChildren(List<Activity> children) {
        this.children = children;
    }

    public List<Activity> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(List<Activity> predecessors) {
        this.predecessors = predecessors;
    }

    public void setTimeScale(TimeScale timeScale) {
        this.timeScale = timeScale;
    }
}

