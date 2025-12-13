package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.enums.Queue;
import com.example.projeto_tcc.enums.VariableType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WorkProductConfigUpdateDTO {
    private Long id;
    private String workProductName;
    private String input_output;
    private String task_name;
    private String queue_name;
    private String queue_type;
    private Integer queue_size;
    private Integer initial_quantity;
    private Queue policy;
    private boolean generate_activity;
    private boolean destroy;
    private VariableType variableType;
    private Long activityId;
    private List<MethodElementObserverDTO> observers;
}
