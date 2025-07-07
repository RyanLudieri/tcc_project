package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.MethodElementDTO;
import com.example.projeto_tcc.dto.ProcessDTO;
import com.example.projeto_tcc.dto.ProcessElementDTO;
import com.example.projeto_tcc.dto.ProcessGetDTO;
import com.example.projeto_tcc.entity.Activity;
import com.example.projeto_tcc.repository.ActivityRepository;
import com.example.projeto_tcc.service.ProcessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/process")
@CrossOrigin(origins = "*") // permitir acesso do front-end
public class ProcessController {

    private final ProcessService service;

    private final ActivityRepository repository;

    public ProcessController(ProcessService service, ActivityRepository repository) {
        this.repository = repository;
        this.service = service;
    }

    @PostMapping
    public Activity createProcess(@RequestBody ProcessDTO dto) {
        return service.saveProcess(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcessGetDTO> getById(@PathVariable Long id) {
        Optional<Activity> process = repository.findById(id);
        return process.map(p -> ResponseEntity.ok(service.convertToGetDTO(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/activity/{id}")
    public ResponseEntity<?> updateActivity(@PathVariable Long id, @RequestBody ProcessElementDTO dto) {
        service.updateGenericActivity(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/method/{id}")
    public ResponseEntity<?> updateMethod(@PathVariable Long id, @RequestBody MethodElementDTO dto) {
        service.updateGenericMethod(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/element/{id}")
    public ResponseEntity<?> deleteElement(@PathVariable Long id) {
        service.deleteElementById(id);
        return ResponseEntity.noContent().build();
    }
}