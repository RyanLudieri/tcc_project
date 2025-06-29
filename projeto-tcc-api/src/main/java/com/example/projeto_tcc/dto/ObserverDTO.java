package com.example.projeto_tcc.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObserverDTO {
    private Long id;
    private String name;
    private String type;
    private ActivitySummaryDTO activity;
    private Long roleId;
    private Long workProductId;

    public ObserverDTO() {}

    public ObserverDTO(Long id, String name, String type, ActivitySummaryDTO activity, Long roleId, Long workProductId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.activity = activity;
        this.roleId = roleId;
        this.workProductId = workProductId;
    }
}

