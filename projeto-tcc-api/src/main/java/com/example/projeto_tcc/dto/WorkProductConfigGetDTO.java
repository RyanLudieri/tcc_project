package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.enums.Queue;
import com.example.projeto_tcc.enums.VariableType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WorkProductConfigGetDTO {
    private Long id;
    private String workProductName;
    private String input_output;
    private String task_name;
    private String queue_name;
    private VariableType variableType;
}
