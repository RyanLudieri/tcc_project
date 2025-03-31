package com.example.projeto_tcc.service;

import com.example.projeto_tcc.model.Iteration;
import com.example.projeto_tcc.repository.IterationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class IterationService {
    private final IterationRepository repository;

    public IterationService(IterationRepository repository) {
        this.repository = repository;
    }

    public Iteration createIteration(Iteration iteration) {
        return repository.save(iteration);
    }

    public List<Iteration> getAllIterations() {
        return repository.findAll();
    }

    public Iteration getIterationById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Iteração não encontrada"));
    }
}
