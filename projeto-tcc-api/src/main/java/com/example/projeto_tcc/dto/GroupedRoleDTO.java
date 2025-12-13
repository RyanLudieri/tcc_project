package com.example.projeto_tcc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class GroupedRoleDTO {
    private String name;
    private String queueName;
    private String queueType;
    private Integer initialQuantity;

    @JsonIgnore
    private List<Long> roleIds;

    public GroupedRoleDTO(String name, String queueName, String queueType, Integer initialQuantity, List<Long> roleIds) {
        this.name = name;
        this.queueName = queueName;
        this.queueType = queueType;
        this.initialQuantity = initialQuantity;
        this.roleIds = roleIds;
    }
}

