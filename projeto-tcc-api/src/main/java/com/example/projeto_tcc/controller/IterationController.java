package com.example.projeto_tcc.controller;


import com.example.projeto_tcc.model.Iteration;
import com.example.projeto_tcc.service.IterationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/iterations")
public class IterationController {
    private final IterationService service;

    public IterationController(IterationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Iteration> createIteration(@RequestBody Iteration iteration) {
        return ResponseEntity.ok(service.createIteration(iteration));
    }

    @GetMapping
    public ResponseEntity<List<Iteration>> getAllIterations() {
        return ResponseEntity.ok(service.getAllIterations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Iteration> getIterationById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getIterationById(id));
    }
}

