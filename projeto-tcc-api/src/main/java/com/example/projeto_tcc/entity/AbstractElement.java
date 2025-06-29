package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.ProcessType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    protected Integer index;


    protected String modelInfo;

    @Enumerated(EnumType.STRING)
    protected ProcessType type;

    public abstract boolean optional();

    public AbstractElement() {
    }

    public AbstractElement(Long id, Integer index, String modelInfo, ProcessType type) {
        this.id = id;
        this.index = index;
        this.modelInfo = modelInfo;
        this.type = type;
    }


}
