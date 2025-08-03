package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.ObserverActivityType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "activity_observer")
public class ActivityObserver extends Observer{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private ObserverActivityType type;
}
