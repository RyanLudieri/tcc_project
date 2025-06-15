package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.entity.Activity;
import com.example.projeto_tcc.enums.ProcessType;
import lombok.Data;

import java.util.List;

@Data
public class ProcessDTO {

    private String name;


    private Integer index;


    private String modelInfo;


    private ProcessType type;


    private List<ProcessElementDTO> processElements;


    private List<MethodElementDTO> methodElements;


    private List<Activity> predecessors; // Corrigido para aceitar qualquer elemento de processo (Activity)


}
