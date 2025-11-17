package com.example.projeto_tcc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessSummaryDTO {
    private Long id;
    private String name;

    private int phases;
    private int iterations;
    private int roles;
    private int activities;
    private int tasks;
    private int artifacts;

    private String lastModified;
}
