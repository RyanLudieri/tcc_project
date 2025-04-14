package com.example.projeto_tcc.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // ou SINGLE_TABLE, veja abaixo
public abstract class ProcessElement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int index;

    @Enumerated(EnumType.STRING)
    private ModelInfo modelInfo;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "parent_id")
    private ProcessElement parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ProcessElement> children = new ArrayList<>();

    public ProcessElement() {}

    public ProcessElement(int index, ModelInfo modelInfo) {
        this.index = index;
        this.modelInfo = modelInfo;
    }
    @Transient
    public abstract boolean optional();

    // Getters e Setters
    public Long getId() { return id; }
    public int getIndex() { return index; }
    public ModelInfo getModelInfo() { return modelInfo; }

    public void setIndex(int index) { this.index = index; }
    public void setModelInfo(ModelInfo modelInfo) { this.modelInfo = modelInfo; }

    public ProcessElement getParent() {
        return parent;
    }

    public void setParent(ProcessElement parent) {
        this.parent = parent;
    }

    public List<ProcessElement> getChildren() {
        return children;
    }

    public void setChildren(List<ProcessElement> children) {
        this.children = children;
    }
}
