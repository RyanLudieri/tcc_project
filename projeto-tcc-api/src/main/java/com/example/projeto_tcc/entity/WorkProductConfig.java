package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.Queue;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class WorkProductConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String workProductName;

    private String input_output;

    private String task_name;

    private String queue_name;

    private String queue_type;

    private Integer queue_size;

    private Integer initial_quantity;

    @Enumerated(EnumType.STRING)
    private Queue policy;

    private boolean generate_activity;

    @ManyToOne
    private Activity activity;

    @OneToMany(mappedBy = "workProductConfig", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MethodElementObserver> observers = new ArrayList<>();

}
