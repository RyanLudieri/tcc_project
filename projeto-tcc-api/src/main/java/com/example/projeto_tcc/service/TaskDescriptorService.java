package com.example.projeto_tcc.service;

import com.example.projeto_tcc.model.TaskDescriptor;
import com.example.projeto_tcc.repository.TaskDescriptorRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskDescriptorService {
    private final TaskDescriptorRepository repository;

    public TaskDescriptorService(TaskDescriptorRepository repository) {
        this.repository = repository;
    }

    public TaskDescriptor createTaskDescriptor(TaskDescriptor taskDescriptor) {
        return repository.save(taskDescriptor);
    }

    public List<TaskDescriptor> getAllTaskDescriptors() {
        return repository.findAll();
    }

    public TaskDescriptor getTaskDescriptorById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("TaskDescriptor não encontrado"));
    }
}

