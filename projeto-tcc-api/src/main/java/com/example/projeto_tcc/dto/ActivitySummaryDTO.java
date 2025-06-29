package com.example.projeto_tcc.dto;

import com.example.projeto_tcc.enums.ProcessType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

}

