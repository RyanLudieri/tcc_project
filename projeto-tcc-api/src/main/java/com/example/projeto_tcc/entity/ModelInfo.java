package com.example.projeto_tcc.entity;

import lombok.Getter;

@Getter
public enum ModelInfo {
    MANDATORY_INPUT("Mandatory Input"),
    OUTPUT("Output"),
    PRIMARY_PERFORMER("Primary Performer");

    private final String value;

    ModelInfo(String value) {
        this.value = value;
    }


}
