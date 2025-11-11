package com.example.projeto_tcc.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@Entity
public class RoleConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String queue_name;

    private String queue_type;

    private Integer initial_quantity;

    @ElementCollection
    private List<Long> roleIds; // IDs dos Roles que compartilham esse nome

    @OneToMany(mappedBy = "roleConfig", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<MethodElementObserver> observers = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_process_id")
    @JsonBackReference
    private DeliveryProcess deliveryProcess;

    @ManyToMany
    @JoinTable(
            name = "role_config_activity",
            joinColumns = @JoinColumn(name = "role_config_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id")
    )
    private Set<Activity> activities = new HashSet<>();



}
