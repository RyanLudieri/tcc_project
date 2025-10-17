package com.example.projeto_tcc.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class DeliveryProcess extends Process {
    @OneToMany(mappedBy = "deliveryProcess", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<RoleConfig> roleConfigs = new ArrayList<>();

    @OneToMany(mappedBy = "deliveryProcess", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<WorkProductConfig> workProductConfigs = new ArrayList<>();

    @OneToMany(mappedBy = "deliveryProcess", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ActivityConfig> activityConfigs = new ArrayList<>();

    @OneToMany(mappedBy = "deliveryProcess", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<GeneratorConfig> generatorConfigs = new ArrayList<>();

}


