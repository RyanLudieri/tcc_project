package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.enums.MethodType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MethodElementDTO {
    private String name;
    private MethodType type;

    private String modelInfo;
    private Integer parentIndex; // índice da Activity pai, se houver

}
