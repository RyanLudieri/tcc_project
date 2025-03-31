package com.example.projeto_tcc.service;

import com.example.projeto_tcc.model.Phase;
import com.example.projeto_tcc.repository.PhaseRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PhaseService {
    private final PhaseRepository repository;

    public PhaseService(PhaseRepository repository) {
        this.repository = repository;
    }

    public Phase createPhase(Phase phase) {
        return repository.save(phase);
    }

    public List<Phase> getAllPhases() {
        return repository.findAll();
    }

    public Phase getPhaseById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fase não encontrada"));
    }
}

