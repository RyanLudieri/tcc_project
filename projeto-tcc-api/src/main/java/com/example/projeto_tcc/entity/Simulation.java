package com.example.projeto_tcc.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Simulation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "delivery_process_id")
    @JsonManagedReference
    private DeliveryProcess deliveryProcess;

    @Column(columnDefinition = "TEXT")
    private String objective;
}

