package com.example.projeto_tcc.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Data
public class ReplicationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer replicationNumber;
    private Double duration;

    @ElementCollection
    @CollectionTable(name = "replication_queue_stats", joinColumns = @JoinColumn(name = "replication_id"))
    @MapKeyColumn(name = "queue_name")
    @Column(name = "final_count")
    private Map<String, Integer> queueFinalCounts = new HashMap<>();

    @ManyToOne
    @JoinColumn(name = "global_result_id")
    private GlobalSimulationResult globalResult;


    @OneToMany(mappedBy = "replication", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceUsageEvent> resourceEvents = new ArrayList<>();

}
