package com.example.projeto_tcc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class DeliveryProcess extends ProcessElement {

    @ManyToMany
    @JoinTable(
            name = "delivery_process_predecessors",
            joinColumns = @JoinColumn(name = "delivery_process_id"),
            inverseJoinColumns = @JoinColumn(name = "predecessor_id")
    )
    private List<DeliveryProcess> predecessors = new ArrayList<>();

    public DeliveryProcess() {}

    public DeliveryProcess(int index, ModelInfo modelInfo) {
        super(index, modelInfo);
    }

    @Override
    public String method(ProcessType type) {
        return "DeliveryProcess method called with type: " + type;
    }

    @Override
    public boolean optional() {
        return false;
    }

    public List<DeliveryProcess> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(List<DeliveryProcess> predecessors) {
        this.predecessors = predecessors;
    }

}
