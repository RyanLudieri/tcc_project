package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.entity.Activity;
import com.example.projeto_tcc.enums.ProcessType;
import lombok.Data;

import java.util.List;

@Data
public class ProcessGetDTO {
    private String name;
    private Integer index;
    private String modelInfo;
    private ProcessType type;
    private List<ProcessElementDTO> processElements;
    private List<Activity> predecessors;
    // NÃO inclui methodElements
}
