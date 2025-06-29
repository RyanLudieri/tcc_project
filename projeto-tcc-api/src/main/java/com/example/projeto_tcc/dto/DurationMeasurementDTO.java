package com.example.projeto_tcc.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DurationMeasurementDTO {
    private int id;
    private String name;
    private double value;
    private ActivitySummaryDTO activity;

    public DurationMeasurementDTO() {
    }

    public DurationMeasurementDTO(int id, String name, double value, ActivitySummaryDTO activity) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.activity = activity;
    }


}

