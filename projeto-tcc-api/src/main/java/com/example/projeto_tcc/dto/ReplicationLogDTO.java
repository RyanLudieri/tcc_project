package com.example.projeto_tcc.dto;

import lombok.Data;
import java.util.Map;

@Data
public class ReplicationLogDTO {
    private Integer replicationNumber;
    private Double duration;

    private Map<String, Integer> queueFinalCounts;
}
