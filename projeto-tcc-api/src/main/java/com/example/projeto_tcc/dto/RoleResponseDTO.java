package com.example.projeto_tcc.dto;

public class RoleResponseDTO {
    private Long id;
    private String name;
    private String queueName;
    private String queueType;
    private Integer initialQuantity;

    // construtores, getters e setters
    public RoleResponseDTO() {}

    public RoleResponseDTO(Long id, String name, String queueName, String queueType, Integer initialQuantity) {
        this.id = id;
        this.name = name;
        this.queueName = queueName;
        this.queueType = queueType;
        this.initialQuantity = initialQuantity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getQueueName() { return queueName; }
    public void setQueueName(String queueName) { this.queueName = queueName; }

    public String getQueueType() { return queueType; }
    public void setQueueType(String queueType) { this.queueType = queueType; }

    public Integer getInitialQuantity() { return initialQuantity; }
    public void setInitialQuantity(Integer initialQuantity) { this.initialQuantity = initialQuantity; }
}
