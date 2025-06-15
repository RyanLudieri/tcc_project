package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.dto.SimulationParamsDTO;
import jakarta.persistence.Entity;

@Entity
public class Milestone extends Activity{
    @Override
    public boolean optional() {
        return true;
    }

    public void configureFromDTO(SimulationParamsDTO dto) {
        this.setDependencyType(dto.getDependencyType());
        this.setConditionToProcess(dto.getConditionToProcess());
        this.setProcessingQuantity(dto.getProcessingQuantity());
        this.setSample(null);
        this.setObservers(null);
    }

}
