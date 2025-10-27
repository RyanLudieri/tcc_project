package com.example.projeto_tcc.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneratorConfigDTO {

    private Long id;
    private String distributionType;

    private DistributionParameterDTO distribution;
    private WorkProductConfigSummaryDTO targetWorkProduct;

    private List<GenerateObserverDTO> observers = new ArrayList<>();
}
