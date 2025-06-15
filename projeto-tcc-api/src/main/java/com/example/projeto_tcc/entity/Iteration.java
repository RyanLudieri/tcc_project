package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.dto.SimulationParamsDTO;
import jakarta.persistence.Entity;

@Entity
public class Iteration extends Activity{

    @Override
    public boolean optional() {
        return false;
    }

    public void configureFromDTO(SimulationParamsDTO dto) {
        this.setTimeBox(dto.getTimeBox());
        this.setTimeScale(dto.getTimeScale());
        this.setConditionToProcess(dto.getConditionToProcess());
        this.setProcessingQuantity(dto.getProcessingQuantity());
        this.setIterationBehavior(dto.getIterationBehavior());
        // Sample e Observer setados fora
    }
}
