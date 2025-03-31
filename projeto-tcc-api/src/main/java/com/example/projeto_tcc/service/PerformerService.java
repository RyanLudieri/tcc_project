package com.example.projeto_tcc.service;

import com.example.projeto_tcc.model.Performer;
import com.example.projeto_tcc.repository.PerformerRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PerformerService {
    private final PerformerRepository repository;

    public PerformerService(PerformerRepository repository) {
        this.repository = repository;
    }

    public Performer createPerformer(Performer performer) {
        return repository.save(performer);
    }

    public List<Performer> getAllPerformers() {
        return repository.findAll();
    }

    public Performer getPerformerById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Performer não encontrado"));
    }
}

