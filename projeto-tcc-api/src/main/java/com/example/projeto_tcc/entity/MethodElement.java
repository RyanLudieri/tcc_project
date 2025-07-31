package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.MethodType;
import com.example.projeto_tcc.enums.ProcessType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
