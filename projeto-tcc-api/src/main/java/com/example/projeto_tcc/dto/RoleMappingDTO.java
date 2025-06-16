package com.example.projeto_tcc.dto;

import java.util.List;

public class RoleMappingDTO {
    private Long roleId;
    private String queueName;
    private String queueType;
    private Integer initialQuantity;

    private List<Long> observerIds; // IDs dos observers relacionados

    public RoleMappingDTO() {
    }

    public RoleMappingDTO(Long roleId, String queueName, String queueType, Integer initialQuantity, List<Long> observerIds) {
        this.roleId = roleId;
        this.queueName = queueName;
        this.queueType = queueType;
        this.initialQuantity = initialQuantity;
        this.observerIds = observerIds;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getQueueType() {
        return queueType;
    }

    public void setQueueType(String queueType) {
        this.queueType = queueType;
    }

    public Integer getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(Integer initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public List<Long> getObserverIds() {
        return observerIds;
    }

    public void setObserverIds(List<Long> observerIds) {
        this.observerIds = observerIds;
    }
}

