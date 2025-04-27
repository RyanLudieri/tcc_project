package com.example.projeto_tcc.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Process extends AbstractElement{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String briefDescription;

    @OneToOne(cascade = CascadeType.ALL)
    private WorkBreakdownStructure wbs;

    public Process() {
    }

    public Process(Long id, int index, List<ProcessElement> predecessors, ModelInfo modelInfo, ProcessType type, Long id1, String name, String briefDescription, WorkBreakdownStructure wbs) {
        super(id, index, predecessors, modelInfo, type);
        this.id = id1;
        this.name = name;
        this.briefDescription = briefDescription;
        this.wbs = wbs;
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

    public String getBriefDescription() {
        return briefDescription;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public WorkBreakdownStructure getWbs() {
        return wbs;
    }

    public void setWbs(WorkBreakdownStructure wbs) {
        this.wbs = wbs;
    }
}

