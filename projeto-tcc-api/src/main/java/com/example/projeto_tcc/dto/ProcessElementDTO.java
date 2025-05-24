package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.entity.ProcessType;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.util.List;

@Data
public class ProcessElementDTO {

    private String name;


    private Integer index;


    private String modelInfo;


    private ProcessType type;


    private List<ProcessElementDTO> children;


    private List<Integer> predecessors; // Agora usa índices
}
