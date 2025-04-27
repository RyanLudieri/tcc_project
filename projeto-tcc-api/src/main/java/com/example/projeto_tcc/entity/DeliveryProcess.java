package com.example.projeto_tcc.entity;

import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class DeliveryProcess extends Process {

    public DeliveryProcess() {
        super();
        this.setType(ProcessType.DELIVERY_PROCESS);
    }

    @Override
    public boolean optional() {
        return false; // Delivery Process normalmente não é opcional no SPEM
    }


}

