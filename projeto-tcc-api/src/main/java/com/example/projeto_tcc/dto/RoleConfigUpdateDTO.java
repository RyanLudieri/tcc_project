package com.example.projeto_tcc.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleConfigUpdateDTO {
    private String name;
    private String queueName;
    private String queueType;
    private Integer initialQuantity;
}

