package com.example.projeto_tcc.dto;

import lombok.Data;

@Data
public class GeneratorConfigDTO {

    private Long workProductConfigId;

    private String distributionType;

    private Double constant;
    private Double mean;
    private Double average;
    private Double standardDeviation;
    private Double low;
    private Double high;
    private Double scale;
    private Double shape;
}
