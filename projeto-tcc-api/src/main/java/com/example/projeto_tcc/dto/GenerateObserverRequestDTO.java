package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.enums.ObserverActivityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateObserverRequestDTO {
    private ObserverActivityType type;
//    private String name;
//    private int position;
}
