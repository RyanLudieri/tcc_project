package com.example.projeto_tcc.dto;

import lombok.Data;

@Data
public class DistributionParameterDTO {
    private Long id;
    private Double constant;
    private Double mean;
    private Double standardDeviation;
    private Double min;
    private Double max;
    private Double shape;
    private Double scale;
    private Double lambda;
    private Double alpha;
    private Double beta;
}

