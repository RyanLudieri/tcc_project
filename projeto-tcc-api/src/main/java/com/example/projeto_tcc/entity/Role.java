package com.example.projeto_tcc.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@DiscriminatorValue("ROLE")
@Entity
@Data
public class Role extends MethodElement{



}
