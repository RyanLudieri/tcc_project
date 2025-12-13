package com.example.projeto_tcc.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SimulationResultDTO {
    private Long id;
    private LocalDateTime executionDate;
    private Integer totalReplications;

    private Double averageDuration;
    private Double durationStdDev;
    private Integer totalRolesUsed;

    private List<QueueMetricDTO> queueMetrics;

    private List<ReplicationLogDTO> logs;
}
