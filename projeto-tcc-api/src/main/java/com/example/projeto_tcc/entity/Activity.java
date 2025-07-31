package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.dto.ActivityResponseDTO;
import com.example.projeto_tcc.dto.SimulationParamsDTO;
import com.example.projeto_tcc.enums.*;
import com.example.projeto_tcc.serializer.CustomElementSerializer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class Activity extends AbstractElement {

    private String name;

    @Enumerated(EnumType.STRING)
    protected ProcessType type;

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
    private ConditionToProcess conditionToProcess;

    @Enumerated(EnumType.STRING)
    private ProcessingQuantity processingQuantity;

    private int timeBox;

    @Getter
    private TimeScale timeScale;

    // --------------------------


    public void configureFromDTO(SimulationParamsDTO dto) {
        this.setTimeBox(dto.getTimeBox());
        this.setTimeScale(dto.getTimeScale());
        this.setConditionToProcess(dto.getConditionToProcess());
        this.setProcessingQuantity(dto.getProcessingQuantity());
        // Sample e Observer setados fora
    }

    // Método polimórfico com valores padrão (Activity “raiz”)
    public ActivityResponseDTO toSimulationDTO() {
        List<Long> observerIds = observers == null ? List.of() : observers.stream()
                .map(Observer::getId)
                .collect(Collectors.toList());

        Integer sampleId = sample != null ? sample.getId() : null;

        return new ActivityResponseDTO(
                this.getId(),
                this.getName(),
                this.getType(),
                null, // requiredResources, se não aplicável aqui
                this.getTimeBox(),
                this.getTimeScale(),
                null, // dependencyType se não aplicável aqui
                this.getConditionToProcess(),
                this.getProcessingQuantity(),
                null, // iterationBehavior não aplicável aqui
                observerIds,
                sampleId
        );
    }







}

