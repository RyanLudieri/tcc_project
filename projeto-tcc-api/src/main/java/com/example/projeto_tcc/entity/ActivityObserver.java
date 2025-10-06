package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.ObserverActivityType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "activity_observer")
public class ActivityObserver extends Observer{

    @Enumerated(EnumType.STRING)
    private ObserverActivityType type;

    @ManyToOne
    @JoinColumn(name = "activity_config_id")
    @JsonBackReference
    private ActivityConfig activityConfig;

}
