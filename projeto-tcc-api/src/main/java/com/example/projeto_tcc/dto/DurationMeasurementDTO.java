package com.example.projeto_tcc.dto;

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

    // Getters e Setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public ActivitySummaryDTO getActivity() {
        return activity;
    }

    public void setActivity(ActivitySummaryDTO activity) {
        this.activity = activity;
    }
}

