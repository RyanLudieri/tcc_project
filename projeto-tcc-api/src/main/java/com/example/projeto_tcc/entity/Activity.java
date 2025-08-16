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

    @Getter
    private TimeScale timeScale;



}

