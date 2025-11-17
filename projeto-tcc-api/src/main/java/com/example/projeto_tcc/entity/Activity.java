package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.ProcessType;
import com.example.projeto_tcc.enums.TimeScale;
import com.example.projeto_tcc.serializer.CustomElementSerializer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;


@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
public class Activity extends AbstractElement {

    private String name;

    @Enumerated(EnumType.STRING)
    protected ProcessType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference("activity-parent")
    @EqualsAndHashCode.Exclude
    private Activity superActivity;

    @OneToMany(mappedBy = "superActivity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("activity-parent")
    private List<Activity> children;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "activity_predecessors",
            joinColumns = @JoinColumn(name = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "predecessor_id")
    )
    @JsonSerialize(using = CustomElementSerializer.class)
    private List<Activity> predecessors;

    @Getter
    private TimeScale timeScale;

    @OneToOne(mappedBy = "activity",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @JsonManagedReference("activity-config")
    @JsonIgnore
    private ActivityConfig activityConfig;



}

