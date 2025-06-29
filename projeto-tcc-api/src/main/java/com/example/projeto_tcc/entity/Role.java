package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.ProcessType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@DiscriminatorValue("ROLE")
@Entity
@Getter
@Setter
public class Role extends MethodElement{

    private String queue_name;

    private String queue_type;

    private Integer initial_quantity;

    public Role() {
    }

    public Role(Long id, Integer index, String modelInfo, ProcessType type, Long id1, String name, String queue_name, String queue_type, int initial_quantity, List<Observer> observers) {
        super(id, index, modelInfo, type, id1, name);
        this.queue_name = queue_name;
        this.queue_type = queue_type;
        this.initial_quantity = initial_quantity;
        this.observers = observers;
    }

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private List<Observer> observers;
    @Override
    public boolean optional() {
        return false;
    }


}
