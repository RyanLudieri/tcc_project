package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.ObserverActivityType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "generator_observer")
public class GeneratorObserver extends Observer {

    @Enumerated(EnumType.STRING)
    private ObserverActivityType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generator_config_id")
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    private GeneratorConfig generatorConfig;

}
