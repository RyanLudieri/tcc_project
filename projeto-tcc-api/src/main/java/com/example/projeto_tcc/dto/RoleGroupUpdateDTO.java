package com.example.projeto_tcc.dto;


import lombok.Data;

import java.util.List;

@Data
public class RoleGroupUpdateDTO {
    private List<Long> roleIds;
    private String queueName;
    private String queueType;
    private Integer initialQuantity;
    private List<Long> observerIds;


}

