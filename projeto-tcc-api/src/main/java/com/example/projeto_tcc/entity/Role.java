package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.ProcessType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.List;

@DiscriminatorValue("ROLE")
@Entity
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



    public String getQueue_name() {
        return queue_name;
    }

    public void setQueue_name(String queue_name) {
        this.queue_name = queue_name;
    }

    public String getQueue_type() {
        return queue_type;
    }

    public void setQueue_type(String queue_type) {
        this.queue_type = queue_type;
    }

    public int getInitial_quantity() {
        return initial_quantity;
    }

    public void setInitial_quantity(int initial_quantity) {
        this.initial_quantity = initial_quantity;
    }

    public List<Observer> getObservers() {
        return observers;
    }

    public void setObservers(List<Observer> observers) {
        this.observers = observers;
    }
}
