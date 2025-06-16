package com.example.projeto_tcc.entity;

import jakarta.persistence.*;

@Entity
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
