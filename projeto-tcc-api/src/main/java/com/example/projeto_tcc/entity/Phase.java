package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.dto.ActivityResponseDTO;
import com.example.projeto_tcc.dto.SimulationParamsDTO;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Phase extends Activity{
    @Override
    public boolean optional() {
        return true;
    }

    public void configureFromDTO(SimulationParamsDTO dto) {
        this.setTimeBox(dto.getTimeBox());
        this.setTimeScale(dto.getTimeScale());
        this.setConditionToProcess(dto.getConditionToProcess());
        this.setProcessingQuantity(dto.getProcessingQuantity());
        this.setObservers(null);
        this.setSample(null);
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
                null,    // o campo exclusivo do Iteration
                List.of(),
                null
        );
    }

}
