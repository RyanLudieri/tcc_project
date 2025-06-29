package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.enums.ProcessType;
import com.example.projeto_tcc.enums.Queue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkProductResponseDTO {
    private Long id;
    private String name;
    private String modelInfo;
    private ProcessType type;
    private String taskName;
    private String queueName;
    private String queueType;
    private Integer queueSize;
    private Integer initialQuantity;
    private Queue policy;

    public WorkProductResponseDTO(Long id, String name, String modelInfo, ProcessType type,
                                  String taskName, String queueName, String queueType,
                                  Integer queueSize, Integer initialQuantity, Queue policy) {
        this.id = id;
        this.name = name;
        this.modelInfo = modelInfo;
        this.type = type;
        this.taskName = taskName;
        this.queueName = queueName;
        this.queueType = queueType;
        this.queueSize = queueSize;
        this.initialQuantity = initialQuantity;
        this.policy = policy;
    }
}

