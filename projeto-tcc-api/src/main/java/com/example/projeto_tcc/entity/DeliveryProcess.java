package com.example.projeto_tcc.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class DeliveryProcess extends Process {

    @OneToMany(mappedBy = "deliveryProcess", cascade = CascadeType.ALL)
    @JsonManagedReference
    @JsonIgnore
    private List<RoleConfig> roleConfigs = new ArrayList<>();

    @OneToMany(mappedBy = "deliveryProcess", cascade = CascadeType.ALL)
    @JsonManagedReference
    @JsonIgnore
    private List<WorkProductConfig> workProductConfigs = new ArrayList<>();

    @OneToMany(mappedBy = "deliveryProcess", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @JsonIgnore
    private List<ActivityConfig> activityConfigs = new ArrayList<>();

    @OneToMany(mappedBy = "deliveryProcess", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @JsonIgnore
    private List<GeneratorConfig> generatorConfigs = new ArrayList<>();

    @OneToMany(mappedBy = "deliveryProcess", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PhaseConfig> phaseConfigs = new ArrayList<>();
    @Transient
    private List<ProcessElement> processElements = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "simulation_id")
    @JsonBackReference
    private Simulation simulation;

}


