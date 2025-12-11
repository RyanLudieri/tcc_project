package com.example.projeto_tcc.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueueMetricDTO {
    private String queueName;
    private Double averageSize;
    private Double stdDev;
    private String taskName;

}
