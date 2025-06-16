package com.example.projeto_tcc.dto;

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

    public Long getRoleId() {
        return roleId;
    }

    public String getQueueName() {
        return queueName;
    }

    public String getQueueType() {
        return queueType;
    }

    public Integer getInitialQuantity() {
        return initialQuantity;
    }
}

