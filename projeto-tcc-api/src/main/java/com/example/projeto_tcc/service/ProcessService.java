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

    private int currentIndex = 0; // CONTADOR de índice

    public ProcessService(ProcessRepository repository) {
        this.repository = repository;
    }

    public Process saveProcess(ProcessDTO dto) {
        currentIndex = 0; // zera o contador no começo do POST

        DeliveryProcess deliveryProcess = new DeliveryProcess();
        deliveryProcess.setName(dto.getName());
        deliveryProcess.setPredecessors(dto.getPredecessors());
        deliveryProcess.setType(ProcessType.DELIVERY_PROCESS);
        deliveryProcess.setIndex(currentIndex);
        deliveryProcess.optional();
        currentIndex++; // <-- Incrementa aqui depois de setar o DeliveryProcess

        WorkBreakdownStructure wbs = new WorkBreakdownStructure();
        List<ProcessElement> elements = new ArrayList<>();

        for (ProcessElementDTO elemDto : dto.getProcessElements()) {
            ProcessElement element = toEntity(elemDto);
            elements.add(element);
        }
        wbs.setProcessElements(elements);
        deliveryProcess.setWbs(wbs);

        currentIndex = 0; // opcional: reseta depois de salvar também
        return repository.save(deliveryProcess);
    }

    private ProcessElement toEntity(ProcessElementDTO dto) {
        ProcessElement entity = createProcessElementByType(dto.getType(), dto);
        entity.setName(dto.getName());
        entity.setPredecessors(dto.getPredecessors());
        entity.optional();

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

    private ProcessElement createProcessElementByType(ProcessType type, ProcessElementDTO dto) {
        switch (type) {
            case ACTIVITY:
                Activity activity = new Activity();
                activity.setIndex(currentIndex++);
                activity.setType(ProcessType.ACTIVITY);
                return activity;
            case TASK_DESCRIPTOR:
                TaskDescriptor task = new TaskDescriptor();
                task.setIndex(currentIndex++);
                task.setType(ProcessType.TASK_DESCRIPTOR);
                return task;
            case MILESTONE:
                Milestone milestone = new Milestone();
                milestone.setIndex(currentIndex++);
                milestone.setType(ProcessType.MILESTONE);
                return milestone;
            case PHASE:
                Phase phase = new Phase();
                phase.setIndex(currentIndex++);
                phase.setType(ProcessType.PHASE);
                return phase;
            case ITERATION:
                Iteration iteration = new Iteration();
                iteration.setIndex(currentIndex++);
                iteration.setType(ProcessType.ITERATION);
                return iteration;
            case WORKPRODUCT:
                WorkProduct workProduct = new WorkProduct();
                workProduct.setModelInfo(dto.getModelInfo());
                return workProduct;
            case PERFORMER:
                Performer performer = new Performer();
                performer.setModelInfo(dto.getModelInfo());
                return performer;
            default:
                throw new IllegalArgumentException("Tipo de elemento de processo não suportado: " + type);
        }
    }


    public List<Process> getAllProcesses() {
        return repository.findAll();
    }
}

