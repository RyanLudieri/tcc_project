package com.example.projeto_tcc.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class Observer {
    @Id
    @GeneratedValue
    private Long id;
    private String queue_name;
    private String name;
    private Integer position; // posição sequencial







}
