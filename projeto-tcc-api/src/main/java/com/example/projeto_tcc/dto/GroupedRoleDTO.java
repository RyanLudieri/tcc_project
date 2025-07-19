package com.example.projeto_tcc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class GroupedRoleDTO {
    private String name;               // nome do grupo (ex: "Developer")
    private String queueName;          // valor padrão do grupo
    private String queueType;          // valor padrão do grupo
    private Integer initialQuantity;   // valor padrão do grupo

    @JsonIgnore
    private List<Long> roleIds;        // lista só com ids das Roles do grupo

    // construtores, getters e setters

    public GroupedRoleDTO(String name, String queueName, String queueType, Integer initialQuantity, List<Long> roleIds) {
        this.name = name;
        this.queueName = queueName;
        this.queueType = queueType;
        this.initialQuantity = initialQuantity;
        this.roleIds = roleIds;
    }
}

