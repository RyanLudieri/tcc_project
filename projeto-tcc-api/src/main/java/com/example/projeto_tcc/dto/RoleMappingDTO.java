package com.example.projeto_tcc.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleMappingDTO {
    private Long roleId;
    private String queueName;
    private String queueType;
    private Integer initialQuantity;

    private List<Long> observerIds;

    public RoleMappingDTO() {
    }

    public RoleMappingDTO(Long roleId, String queueName, String queueType, Integer initialQuantity, List<Long> observerIds) {
        this.roleId = roleId;
        this.queueName = queueName;
        this.queueType = queueType;
        this.initialQuantity = initialQuantity;
        this.observerIds = observerIds;
    }

}

