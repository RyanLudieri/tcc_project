package com.example.projeto_tcc.dto;

import lombok.Data;

import java.util.List;

@Data
public class GroupedRoleDTO {
    private String roleName;
    private List<Long> roleIds;

    public GroupedRoleDTO(String roleName, List<Long> roleIds) {
        this.roleName = roleName;
        this.roleIds = roleIds;
    }

    // Getters e setters, ou use @Data se estiver usando Lombok
}
