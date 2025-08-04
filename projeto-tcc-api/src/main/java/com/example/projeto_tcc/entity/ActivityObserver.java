package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.ObserverActivityType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "activity_observer")
public class ActivityObserver extends Observer{

    private ObserverActivityType type;

    @ManyToOne
    @JoinColumn(name = "activity_config_id")
    private ActivityConfig activityConfig;
}
