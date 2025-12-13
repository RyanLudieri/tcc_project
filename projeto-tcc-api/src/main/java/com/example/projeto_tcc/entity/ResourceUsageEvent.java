package com.example.projeto_tcc.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ResourceUsageEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String resourceName;
    private String activityName;

    private double startTime;
    private double endTime;

    private int quantityUsed;

    @ManyToOne
    @JoinColumn(name = "replication_id")
    private ReplicationResult replication;

}
