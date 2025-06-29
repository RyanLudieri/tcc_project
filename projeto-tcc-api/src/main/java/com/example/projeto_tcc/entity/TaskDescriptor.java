package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.dto.ActivityResponseDTO;
import com.example.projeto_tcc.dto.SimulationParamsDTO;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class TaskDescriptor extends Activity{

    private int requiredResources;
    @Override
    public boolean optional() {
        return false;
    }

    public void configureFromDTO(SimulationParamsDTO dto) {
        this.setConditionToProcess(dto.getConditionToProcess());
        this.setProcessingQuantity(dto.getProcessingQuantity());
        this.setRequiredResources(dto.getRequiredResources());
    }

    @Override
    public ActivityResponseDTO toSimulationDTO() {
        ActivityResponseDTO baseDTO = super.toSimulationDTO();
        return new ActivityResponseDTO(
                baseDTO.getId(),
                baseDTO.getName(),
                baseDTO.getType(),
                this.getRequiredResources(),
                baseDTO.getTimeBox(),
                baseDTO.getTimeScale(),
                null,
                baseDTO.getConditionToProcess(),
                baseDTO.getProcessingQuantity(),
                null,    // o campo exclusivo do Iteration
                baseDTO.getObserverIds(),
                baseDTO.getSampleId()
        );
    }

}
