package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.model.Performer;
import com.example.projeto_tcc.service.PerformerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performers")
public class PerformerController {
    private final PerformerService service;

    public PerformerController(PerformerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Performer> createPerformer(@RequestBody Performer performer) {
        return ResponseEntity.ok(service.createPerformer(performer));
    }

    @GetMapping
    public ResponseEntity<List<Performer>> getAllPerformers() {
        return ResponseEntity.ok(service.getAllPerformers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Performer> getPerformerById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getPerformerById(id));
    }
}

