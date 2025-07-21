package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.dto.ActivityResponseDTO;
import com.example.projeto_tcc.dto.SimulationParamsDTO;
import com.example.projeto_tcc.enums.DependencyType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Milestone extends Activity{

    @Enumerated(EnumType.STRING)
    private DependencyType dependencyType;

    public void configureFromDTO(SimulationParamsDTO dto) {
        this.setDependencyType(dto.getDependencyType());
        this.setConditionToProcess(dto.getConditionToProcess());
        this.setProcessingQuantity(dto.getProcessingQuantity());
        this.setSample(null);
        this.setObservers(null);
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
                this.getDependencyType(),
                baseDTO.getConditionToProcess(),
                baseDTO.getProcessingQuantity(),
                null,
                List.of(),
                null
        );
    }


}
