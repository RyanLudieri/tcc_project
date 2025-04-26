package com.example.projeto_tcc.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProcessDTO {
    private String name;
    private String briefDescription;
    private List<ProcessElementDTO> processElements;
}