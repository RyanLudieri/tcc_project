package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.ProcessDTO;
import com.example.projeto_tcc.dto.ProcessElementDTO;
import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.entity.Process;
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
        DeliveryProcess deliveryProcess = new DeliveryProcess();
        deliveryProcess.setName(dto.getName());
        deliveryProcess.setPredecessors(dto.getPredecessors());
        deliveryProcess.setModelInfo(dto.getModelInfo());
        deliveryProcess.setType(ProcessType.DELIVERY_PROCESS);
        deliveryProcess.setIndex(dto.getIndex());

        WorkBreakdownStructure wbs = new WorkBreakdownStructure();
        List<ProcessElement> elements = new ArrayList<>();

        for (ProcessElementDTO elemDto : dto.getProcessElements()) {
            ProcessElement element = toEntity(elemDto);
            elements.add(element);
        }
        wbs.setProcessElements(elements);
        deliveryProcess.setWbs(wbs);

        return repository.save(deliveryProcess);
    }

    private ProcessElement toEntity(ProcessElementDTO dto) {
        ProcessElement entity = createProcessElementByType(dto.getType());
        entity.setName(dto.getName());
        entity.setPredecessors(dto.getPredecessors());
        entity.setModelInfo(dto.getModelInfo());
        entity.setIndex(dto.getIndex());

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

    private ProcessElement createProcessElementByType(ProcessType type) {
        switch (type) {
            case ACTIVITY:
                Activity activity = new Activity();
                activity.setType(ProcessType.ACTIVITY);
                return activity;
            case TASK_DESCRIPTOR:
                TaskDescriptor task = new TaskDescriptor();
                task.setType(ProcessType.TASK_DESCRIPTOR);
                return task;
            case MILESTONE:
                Milestone milestone = new Milestone();
                milestone.setType(ProcessType.MILESTONE);
                return milestone;
            case PHASE:
                Phase phase = new Phase();
                phase.setType(ProcessType.PHASE);
                return phase;
            case ITERATION:
                Iteration iteration = new Iteration();
                iteration.setType(ProcessType.ITERATION);
                return iteration;
            default:
                throw new IllegalArgumentException("Tipo de elemento de processo não suportado: " + type);
        }
    }


    public List<Process> getAllProcesses() {
        return repository.findAll();
    }
}

