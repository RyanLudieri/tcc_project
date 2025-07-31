package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.MethodType;
import com.example.projeto_tcc.enums.ProcessType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Data
public abstract class AbstractElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    protected Integer index;


    protected String modelInfo;

    protected boolean optional;


}
