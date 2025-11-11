package com.example.projeto_tcc.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Simulation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "simulation", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DeliveryProcess> processes = new ArrayList<>();

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

