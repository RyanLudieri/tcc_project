package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.MethodType;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class MethodElement extends AbstractElement{

    private String name;

    @ManyToOne
    private Activity parentActivity;

    @Enumerated(EnumType.STRING)
    private MethodType methodType;

}
