// Em GeneratorConfig.java
package com.example.projeto_tcc.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

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
    @JsonBackReference // Evita loops infinitos ao converter para JSON
    private DeliveryProcess deliveryProcess;
}