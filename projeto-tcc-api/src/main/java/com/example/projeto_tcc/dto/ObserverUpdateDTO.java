package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.enums.ObserverMethodElementType;
import lombok.Data;

@Data
public class ObserverUpdateDTO {

    private ObserverMethodElementType type;
    private String queueName;

}
