package com.example.projeto_tcc.dto;

public class SimulationCreateDTO {
    private String objective;

    public SimulationCreateDTO() {}

    public SimulationCreateDTO(String objective) {
        this.objective = objective;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }
}
