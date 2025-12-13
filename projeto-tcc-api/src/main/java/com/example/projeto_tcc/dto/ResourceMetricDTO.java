package com.example.projeto_tcc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResourceMetricDTO {
    private String resourceName;
    private double averageUtilization;
    private double stdDev;

    public ResourceMetricDTO() {

    }
}
