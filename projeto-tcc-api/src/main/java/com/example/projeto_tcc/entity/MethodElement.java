package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.MethodType;
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

    public MethodElement() {
    }

    public MethodElement(String name) {
        this.name = name;
    }

    public MethodElement(Long id, Integer index, String modelInfo, ProcessType type, Long id1, String name, boolean optional) {
        super(id, index, modelInfo, type, optional);
        this.name = name;
    }

}
