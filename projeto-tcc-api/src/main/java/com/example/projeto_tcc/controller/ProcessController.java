package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.MethodElementDTO;
import com.example.projeto_tcc.dto.ProcessDTO;
import com.example.projeto_tcc.dto.ProcessElementDTO;
import com.example.projeto_tcc.entity.Activity;
import com.example.projeto_tcc.entity.DeliveryProcess;
import com.example.projeto_tcc.entity.Process;
import com.example.projeto_tcc.repository.ProcessRepository;
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

    private final ProcessRepository repository;

    public ProcessController(ProcessService service, ProcessRepository repository) {
        this.repository = repository;
        this.service = service;
    }

    @PostMapping
    public Activity createProcess(@RequestBody ProcessDTO dto) {
        return service.saveProcess(dto);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Activity> getById(@PathVariable Long id) {
        Optional<Activity> process = repository.findById(id);
        return process.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Activity> getAllProcesses() {
        return service.getAllProcesses();
    }

    @PutMapping("/activity/{id}")
    public ResponseEntity<?> updateActivity(@PathVariable Long id, @RequestBody ProcessElementDTO dto) {
        return ResponseEntity.ok(service.updateGenericActivity(id, dto));
    }

    @PutMapping("/method/{id}")
    public ResponseEntity<?> updateMethod(@PathVariable Long id, @RequestBody MethodElementDTO dto) {
        return ResponseEntity.ok(service.updateGenericMethod(id, dto));
    }

    @DeleteMapping("/element/{id}")
    public ResponseEntity<?> deleteElement(@PathVariable Long id) {
        service.deleteElementById(id);
        return ResponseEntity.noContent().build();
    }
}