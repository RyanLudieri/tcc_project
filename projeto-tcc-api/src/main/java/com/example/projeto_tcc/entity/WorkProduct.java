package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.ProcessType;
import com.example.projeto_tcc.enums.Queue;
import com.example.projeto_tcc.enums.WorkProductType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Data
public class WorkProduct extends MethodElement{

    private String input_output;

    private String task_name;

    private String queue_name;

    private String queue_type;

    private Integer queue_size;

    private Integer initial_quantity;

    @Enumerated(EnumType.STRING)
    private Queue policy;

    @OneToMany(mappedBy = "workproduct", cascade = CascadeType.ALL)
    private List<Observer> observers;

    @Enumerated(EnumType.STRING)
    private WorkProductType workProductType;



}
