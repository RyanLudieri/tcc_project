package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.enums.ProcessType;
import com.example.projeto_tcc.enums.Queue;
import com.example.projeto_tcc.enums.WorkProductType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkProductResponseDTO {
    private Long id;
    private String name;
    private String input_output;
    private String taskName;
    private String queueName;
    private String queueType;
    private Integer queueSize;
    private Integer initialQuantity;
    private Queue policy;
    private WorkProductType workProductType;

    public WorkProductResponseDTO(Long id, String name, String input_output, String taskName, String queueName, String queueType, Integer queueSize, Integer initialQuantity, Queue policy, WorkProductType workProductType) {
        this.id = id;
        this.name = name;
        this.input_output = input_output;
        this.taskName = taskName;
        this.queueName = queueName;
        this.queueType = queueType;
        this.queueSize = queueSize;
        this.initialQuantity = initialQuantity;
        this.policy = policy;
        this.workProductType = workProductType;
    }
}

