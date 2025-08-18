package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.enums.ObserverActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActivityObserverDTO {
    private Long id;
    private String name;
    private String queueName;
    private int position;
    private ObserverActivityType type;
}

