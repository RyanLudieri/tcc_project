package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.dto.SimulationParamsDTO;
import jakarta.persistence.Entity;

@Entity
public class TaskDescriptor extends Activity{
    @Override
    public boolean optional() {
        return false;
    }

    public void configureFromDTO(SimulationParamsDTO dto) {
        this.setConditionToProcess(dto.getConditionToProcess());
        this.setProcessingQuantity(dto.getProcessingQuantity());
        this.setRequiredResources(dto.getRequiredResources());
    }
}
