package com.example.projeto_tcc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Observer {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String type;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "workproduct_id")
    private WorkProduct workproduct;


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


}
