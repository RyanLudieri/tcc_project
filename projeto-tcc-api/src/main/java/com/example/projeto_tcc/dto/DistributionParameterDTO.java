package com.example.projeto_tcc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class DistributionParameterDTO {
    private Long id;
    private Double constant;
    private Double mean;
    private Double average;
    private Double standardDeviation;
    private Double low;
    private Double high;
    private Double shape;
    private Double scale;
}

