package com.example.projeto_tcc.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProcessElementDTO {
    private String name;
    private String briefDescription;
    private double completeness;
    private List<ProcessElementDTO> children;
    private List<Long> predecessorsIds; // apenas IDs para associar
}
