package com.example.projeto_tcc.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Observer {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String type;

    @ManyToOne
    private Activity activity;

    public Observer() {
    }

    public Observer(Long id, String name, String type, Activity activity) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.activity = activity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
