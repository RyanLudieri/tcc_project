package com.example.projeto_tcc.service;

import com.example.projeto_tcc.model.ProcessType;
import com.example.projeto_tcc.model.TaskDescriptor;
import com.example.projeto_tcc.repository.TaskDescriptorRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskDescriptorService {
    private final TaskDescriptorRepository repository;

    private final ProcessElementIndexService indexService;

    public TaskDescriptorService(TaskDescriptorRepository repository,ProcessElementIndexService indexService) {
        this.repository = repository;
        this.indexService = indexService;
    }

    public TaskDescriptor createTaskDescriptor(TaskDescriptor taskDescriptor) {
        int index = indexService.getNextIndex();
        taskDescriptor.setIndex(index);
        taskDescriptor.setType(ProcessType.TASK_DESCRIPTOR);
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

