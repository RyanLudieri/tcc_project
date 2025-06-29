package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.entity.DistributionParameter;
import com.example.projeto_tcc.enums.BestFitDistribution;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class SampleDTO {
    private Integer id;
    private String name;
    private int size;
    private BestFitDistribution distribution;
    private DistributionParameter parameter;
    private List<DurationMeasurementDTO> measurements;

    public SampleDTO() {}

    public SampleDTO(Integer id, String name, int size, BestFitDistribution distribution,
                     DistributionParameter parameter, List<DurationMeasurementDTO> measurements) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.distribution = distribution;
        this.parameter = parameter;
        this.measurements = measurements;
    }


}

