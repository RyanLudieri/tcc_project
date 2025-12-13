package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.enums.ObserverMethodElementType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MethodElementObserverDTO {
    private Long id;
    private String queue_name;
    private String name;
    private int position;
    private ObserverMethodElementType type;
    private Long workProductConfigId;
}
