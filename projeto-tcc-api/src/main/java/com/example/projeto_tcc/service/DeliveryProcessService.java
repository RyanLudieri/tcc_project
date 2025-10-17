package com.example.projeto_tcc.service;

import com.example.projeto_tcc.entity.DeliveryProcess;
import com.example.projeto_tcc.repository.DeliveryProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryProcessService {

    private final DeliveryProcessRepository repository;

    public DeliveryProcess getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("DeliveryProcess não encontrado"));
    }
}
