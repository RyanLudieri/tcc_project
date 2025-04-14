package com.example.projeto_tcc.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("DELIVERY")
public class DeliveryProcess extends ProcessElement {

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "delivery_process_predecessors",
            joinColumns = @JoinColumn(name = "delivery_process_id"),
            inverseJoinColumns = @JoinColumn(name = "predecessor_id")
    )
    private List<DeliveryProcess> predecessors = new ArrayList<>();

    @Enumerated(EnumType.STRING) // Salva como texto no banco de dados
    @Column(name = "type", nullable = false)
    private ProcessType type;


    public DeliveryProcess() {}

    public DeliveryProcess(int index, ModelInfo modelInfo) {
        super(index, modelInfo);
    }


    @Override
    public boolean optional() {
        return true;
    }

    public List<DeliveryProcess> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(List<DeliveryProcess> predecessors) {
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
