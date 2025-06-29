package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.dto.ActivityResponseDTO;
import com.example.projeto_tcc.dto.SimulationParamsDTO;
import com.example.projeto_tcc.enums.IterationBehavior;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Iteration extends Activity{

    @Enumerated(EnumType.STRING)
    private IterationBehavior iterationBehavior;

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

    @Override
    public ActivityResponseDTO toSimulationDTO() {
        ActivityResponseDTO baseDTO = super.toSimulationDTO();
        return new ActivityResponseDTO(
                baseDTO.getId(),
                baseDTO.getName(),
                baseDTO.getType(),
                null,
                baseDTO.getTimeBox(),
                baseDTO.getTimeScale(),
                null,
                baseDTO.getConditionToProcess(),
                baseDTO.getProcessingQuantity(),
                this.getIterationBehavior(),    // o campo exclusivo do Iteration
                baseDTO.getObserverIds(),
                baseDTO.getSampleId()
        );
    }

}
