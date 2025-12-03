package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.QueueMetricDTO;
import com.example.projeto_tcc.dto.ReplicationLogDTO;
import com.example.projeto_tcc.dto.SimulationResultDTO;
import com.example.projeto_tcc.entity.GlobalSimulationResult;
import com.example.projeto_tcc.repository.GlobalSimulationResultRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ResultsService {

    @Autowired
    private GlobalSimulationResultRepository repository;

    @Transactional
    public SimulationResultDTO getResultById(Long executionId) {
        GlobalSimulationResult entity = repository.findById(executionId).orElse(null);

        if (entity == null) {
            return null;
        }

        SimulationResultDTO dto = new SimulationResultDTO();
        dto.setId(entity.getId());
        dto.setExecutionDate(entity.getExecutionDate());
        dto.setTotalReplications(entity.getTotalReplications());
        dto.setAverageDuration(entity.getAverageDuration());
        dto.setDurationStdDev(entity.getDurationStdDev());

        dto.setQueueMetrics(entity.getQueueStats().stream()
                .map(stat -> new QueueMetricDTO(
                        stat.getQueueName(),
                        stat.getAverageCount(),
                        stat.getStdDevCount()))
                .collect(Collectors.toList()));

        dto.setLogs(entity.getReplicationResults().stream()
                .map(rep -> {
                    ReplicationLogDTO log = new ReplicationLogDTO();
                    log.setReplicationNumber(rep.getReplicationNumber());
                    log.setDuration(rep.getDuration());
                    log.setQueueFinalCounts(rep.getQueueFinalCounts());
                    return log;
                })
                .collect(Collectors.toList()));

        return dto;
    }
}
