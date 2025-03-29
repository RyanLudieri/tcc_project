package com.example.projeto_tcc.service;

import com.example.projeto_tcc.model.DeliveryProcess;
import com.example.projeto_tcc.repository.DeliveryProcessRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryProcessService {
    private final DeliveryProcessRepository repository;

    public DeliveryProcessService(DeliveryProcessRepository repository) {
        this.repository = repository;
    }

    public List<DeliveryProcess> getAll() {
        return repository.findAll();
    }

    public DeliveryProcess create(DeliveryProcess deliveryProcess) {
        return repository.save(deliveryProcess);
    }

    public DeliveryProcess getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }

}
