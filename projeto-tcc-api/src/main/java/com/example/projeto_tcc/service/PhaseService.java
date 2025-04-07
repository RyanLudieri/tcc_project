package com.example.projeto_tcc.service;

import com.example.projeto_tcc.model.Phase;
import com.example.projeto_tcc.model.ProcessType;
import com.example.projeto_tcc.repository.PhaseRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PhaseService {
    private final PhaseRepository repository;

    private final ProcessElementIndexService indexService;

    public PhaseService(PhaseRepository repository, ProcessElementIndexService indexService) {
        this.repository = repository;
        this.indexService = indexService;
    }

    public Phase createPhase(Phase phase) {
        int index = indexService.getNextIndex();
        phase.setIndex(index);
        phase.setType(ProcessType.PHASE);
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

