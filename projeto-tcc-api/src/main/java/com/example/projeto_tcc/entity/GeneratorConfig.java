// Em GeneratorConfig.java
package com.example.projeto_tcc.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
public class GeneratorConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String distributionType;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "distribution_parameter_id", referencedColumnName = "id")
    private DistributionParameter distribution;

    @OneToOne
    @JoinColumn(name = "work_product_config_id", referencedColumnName = "id")
    private WorkProductConfig targetWorkProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_process_id")
    @JsonBackReference
    private DeliveryProcess deliveryProcess;

    @OneToMany(mappedBy = "generatorConfig", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<GeneratorObserver> observers = new HashSet<>();
}