package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.ProcessDTO;
import com.example.projeto_tcc.dto.ProcessElementDTO;
import com.example.projeto_tcc.entity.Process;
import com.example.projeto_tcc.entity.ProcessElement;
import com.example.projeto_tcc.entity.WorkBreakdownStructure;
import com.example.projeto_tcc.repository.ProcessRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessService {

    private final ProcessRepository repository;

    public ProcessService(ProcessRepository repository) {
        this.repository = repository;
    }

    public Process saveProcess(ProcessDTO dto) {
        Process process = new Process();
        process.setName(dto.getName());
        process.setBriefDescription(dto.getBriefDescription());

        WorkBreakdownStructure wbs = new WorkBreakdownStructure();
        List<ProcessElement> elements = new ArrayList<>();

        for (ProcessElementDTO elemDto : dto.getProcessElements()) {
            ProcessElement element = toEntity(elemDto);
            elements.add(element);
        }
        wbs.setProcessElements(elements);
        process.setWbs(wbs);

        return repository.save(process);
    }

    private ProcessElement toEntity(ProcessElementDTO dto) {
        ProcessElement entity = new ProcessElement();
        entity.setName(dto.getName());
        entity.setBriefDescription(dto.getBriefDescription());
        entity.setCompleteness(dto.getCompleteness());

        if (dto.getChildren() != null) {
            List<ProcessElement> children = new ArrayList<>();
            for (ProcessElementDTO childDto : dto.getChildren()) {
                ProcessElement child = toEntity(childDto);
                child.setSuperActivity(entity);
                children.add(child);
            }
            entity.setChildren(children);
        }
        // Obs: aqui você poderia também associar os predecessores pelo ID depois, se precisar
        return entity;
    }

    public List<Process> getAllProcesses() {
        return repository.findAll();
    }
}

