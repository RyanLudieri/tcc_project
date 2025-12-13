package com.example.projeto_tcc.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class GlobalSimulationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long processId;
    private LocalDateTime executionDate;
    private Integer totalReplications;

    private Double averageDuration;
    private Double durationStdDev;

    private Integer totalRolesUsed;

    @OneToMany(mappedBy = "globalResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GlobalQueueStat> queueStats = new ArrayList<>();

    @OneToMany(mappedBy = "globalResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReplicationResult> replicationResults = new ArrayList<>();

    @OneToMany(mappedBy = "globalResult", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<GlobalResourceStat> resourceStats = new ArrayList<>();

    @Lob
    @Column(columnDefinition = "TEXT")
    private String logs;
}