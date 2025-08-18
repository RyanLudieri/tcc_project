package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.enums.ObserverActivityType;

public record ObserverActivityDTO(
        Long id,
        String queueName,
        String name,
        Integer position,
        ObserverActivityType type,
        Long activityConfigId
) {}
