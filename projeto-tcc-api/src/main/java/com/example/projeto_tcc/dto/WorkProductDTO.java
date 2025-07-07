package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.enums.Queue;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkProductDTO {
    private String name;
    private String input_output;
    private Long workProductId;
    private String taskName;
    private String queueName;
    private String queueType;
    private Integer queueSize;
    private Integer initialQuantity;
    private Queue policy;
    private List<Long> observerIds;

}

