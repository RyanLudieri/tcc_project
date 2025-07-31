package com.example.projeto_tcc.entity;

import com.example.projeto_tcc.enums.ProcessType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@DiscriminatorValue("ROLE")
@Entity
@Data
public class Role extends MethodElement{

    private String queue_name;

    private String queue_type;

    private Integer initial_quantity;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private List<Observer> observers;


}
