package com.example.projeto_tcc.service;

import com.example.projeto_tcc.model.Iteration;
import com.example.projeto_tcc.repository.IterationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class IterationService {
    private final IterationRepository repository;

    private final ProcessElementIndexService indexService;

    public IterationService(IterationRepository repository, ProcessElementIndexService indexService) {
        this.repository = repository;
        this.indexService = indexService;
    }

    public Iteration createIteration(Iteration iteration) {
        int index = indexService.getNextIndex();
        iteration.setIndex(index);
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
