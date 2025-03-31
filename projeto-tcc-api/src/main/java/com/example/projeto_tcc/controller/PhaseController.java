package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.model.Phase;
import com.example.projeto_tcc.service.PhaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phases")
public class PhaseController {
    private final PhaseService service;

    public PhaseController(PhaseService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Phase> createPhase(@RequestBody Phase phase) {
        return ResponseEntity.ok(service.createPhase(phase));
    }

    @GetMapping
    public ResponseEntity<List<Phase>> getAllPhases() {
        return ResponseEntity.ok(service.getAllPhases());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Phase> getPhaseById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPhaseById(id));
    }
}

