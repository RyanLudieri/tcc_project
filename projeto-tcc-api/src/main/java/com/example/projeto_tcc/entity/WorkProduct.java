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


    public WorkProduct() {
    }


    public WorkProduct(Long id, Integer index, String modelInfo, ProcessType type, Long id1, String name, String task_name, String queue_name, String queue_type, Integer queue_size, Integer initial_quantity, Queue policy, List<Observer> observers, WorkProductType workProductType, boolean optional) {
        super(id, index, modelInfo, type, id1, name, optional);
        this.task_name = task_name;
        this.queue_name = queue_name;
        this.queue_type = queue_type;
        this.queue_size = queue_size;
        this.initial_quantity = initial_quantity;
        this.policy = policy;
        this.observers = observers;
        this.workProductType = workProductType;
    }


}
