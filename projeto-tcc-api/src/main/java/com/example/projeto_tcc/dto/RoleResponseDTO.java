package com.example.projeto_tcc.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleResponseDTO {
    private Long roleId;
    private String queueName;
    private String queueType;
    private Integer initialQuantity;

    public RoleResponseDTO() {
    }

    public RoleResponseDTO(Long roleId, String queueName, String queueType, Integer initialQuantity) {
        this.roleId = roleId;
        this.queueName = queueName;
        this.queueType = queueType;
        this.initialQuantity = initialQuantity;
    }

}

