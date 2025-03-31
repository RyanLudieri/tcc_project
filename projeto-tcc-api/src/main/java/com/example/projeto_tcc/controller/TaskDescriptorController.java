package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.model.TaskDescriptor;
import com.example.projeto_tcc.service.TaskDescriptorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-descriptors")
public class TaskDescriptorController {
    private final TaskDescriptorService service;

    public TaskDescriptorController(TaskDescriptorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TaskDescriptor> createTaskDescriptor(@RequestBody TaskDescriptor taskDescriptor) {
        return ResponseEntity.ok(service.createTaskDescriptor(taskDescriptor));
    }

    @GetMapping
    public ResponseEntity<List<TaskDescriptor>> getAllTaskDescriptors() {
        return ResponseEntity.ok(service.getAllTaskDescriptors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDescriptor> getTaskDescriptorById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTaskDescriptorById(id));
    }
}

