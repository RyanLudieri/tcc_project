package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.ProcessType;
import jakarta.persistence.Entity;

@Entity
public class WorkProduct extends MethodElement{

    private String task_name;

    private String queue_name;

    private String queue_type;

    private Integer queue_size;

    private Integer initial_quantity;

    private String policy;

    public boolean gererate_activity() {
        return false;
    }

    @Override
    public boolean optional() {
        return false;
    }

    public WorkProduct() {
    }

    public WorkProduct(Long id, Integer index, String modelInfo, ProcessType type, Long id1, String name, String task_name, String queue_name, String queue_type, Integer queue_size, Integer initial_quantity, String policy) {
        super(id, index, modelInfo, type, id1, name);
        this.task_name = task_name;
        this.queue_name = queue_name;
        this.queue_type = queue_type;
        this.queue_size = queue_size;
        this.initial_quantity = initial_quantity;
        this.policy = policy;
    }
}
