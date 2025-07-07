package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.ProcessType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public class MethodElement extends AbstractElement{

    private String name;

    @ManyToOne
    private Activity parentActivity;

    @Override
    public boolean optional() {
        return false;
    }

    public MethodElement() {
    }

    public MethodElement(String name) {
        this.name = name;
    }

    public MethodElement(Long id, Integer index, String modelInfo, ProcessType type, Long id1, String name) {
        super(id, index, modelInfo, type);
        this.name = name;
    }

}
