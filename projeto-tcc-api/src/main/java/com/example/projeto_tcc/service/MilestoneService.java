package com.example.projeto_tcc.service;

import com.example.projeto_tcc.model.Milestone;
import com.example.projeto_tcc.repository.MilestoneRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MilestoneService {
    private final MilestoneRepository repository;

    public MilestoneService(MilestoneRepository repository) {
        this.repository = repository;
    }

    public Milestone createMilestone(Milestone milestone) {
        return repository.save(milestone);
    }

    public List<Milestone> getAllMilestones() {
        return repository.findAll();
    }

    public Milestone getMilestoneById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Milestone não encontrado"));
    }
}

