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

}


