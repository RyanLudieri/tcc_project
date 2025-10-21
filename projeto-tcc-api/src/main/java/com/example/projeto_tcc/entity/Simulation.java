package com.example.projeto_tcc.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

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

    @Column
    private String status;

    @Column
    private LocalDateTime lastModified;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        lastModified = LocalDateTime.now();
    }

}

