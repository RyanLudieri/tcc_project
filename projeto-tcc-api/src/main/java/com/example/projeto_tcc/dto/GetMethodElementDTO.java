package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.enums.MethodType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMethodElementDTO {
    private String name;
    private String modelInfo;
    private Integer parentIndex; // índice da Activity pai, se houver
    private Boolean optional;

}
