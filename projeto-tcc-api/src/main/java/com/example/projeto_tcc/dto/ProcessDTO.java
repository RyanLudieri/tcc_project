package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.entity.ModelInfo;
import com.example.projeto_tcc.entity.ProcessType;
import lombok.Data;

import java.util.List;

@Data
public class ProcessDTO {
    private String name;
    private String briefDescription;
    private Integer index;
    private ModelInfo modelInfo;
    private ProcessType type;
    private List<ProcessElementDTO> processElements;
}
