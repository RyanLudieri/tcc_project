package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.enums.ProcessType;

public class ActivitySummaryDTO {
    private Long id;
    private String name;
    private ProcessType type;

    public ActivitySummaryDTO() {}

    public ActivitySummaryDTO(Long id, String name, ProcessType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ProcessType getType() {
        return type;
    }

    public void setType(ProcessType type) {
        this.type = type;
    }
}

