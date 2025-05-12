package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.MethodElementDTO;
import com.example.projeto_tcc.dto.ProcessDTO;
import com.example.projeto_tcc.dto.ProcessElementDTO;
import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.entity.Process;
import com.example.projeto_tcc.repository.MethodElementRepository;
import com.example.projeto_tcc.repository.ProcessRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProcessService {

    private final ProcessRepository repository;
    private final MethodElementRepository methodElementRepository;

    public ProcessService(ProcessRepository repository, MethodElementRepository methodElementRepository) {
        this.repository = repository;
        this.methodElementRepository = methodElementRepository;
    }

    private int currentIndex = 0;

    // Mapa auxiliar para localizar atividades por índice
    private Map<Integer, Activity> indexToActivity = new HashMap<>();


    public Process saveProcess(ProcessDTO dto) {
        currentIndex = 0;
        indexToActivity.clear();

        DeliveryProcess deliveryProcess = new DeliveryProcess();
        deliveryProcess.setName(dto.getName());
        deliveryProcess.setPredecessors(dto.getPredecessors());
        deliveryProcess.setType(ProcessType.DELIVERY_PROCESS);
        deliveryProcess.setIndex(currentIndex++);
        deliveryProcess.optional();

        WorkBreakdownStructure wbs = new WorkBreakdownStructure();

        // ProcessElements
        List<Activity> elements = new ArrayList<>();
        for (ProcessElementDTO elemDto : dto.getProcessElements()) {
            Activity element = toEntity(elemDto);
            elements.add(element);
        }
        wbs.setProcessElements(elements);

        // MethodElements
        List<MethodElement> methodElements = new ArrayList<>();
        if (dto.getMethodElements() != null) {
            for (MethodElementDTO methodDto : dto.getMethodElements()) {
                MethodElement method = toMethodEntity(methodDto);
                methodElements.add(method);
            }
        }
        wbs.setMethodElements(methodElements);

        deliveryProcess.setWbs(wbs);
        return repository.save(deliveryProcess);
    }

    private Activity toEntity(ProcessElementDTO dto) {
        Activity entity = createProcessElementByType(dto.getType());
        entity.setName(dto.getName());
        entity.setPredecessors(dto.getPredecessors());
        entity.optional();

        // Salva no mapa para referência por index
        indexToActivity.put(entity.getIndex(), entity);

        if (dto.getChildren() != null) {
            List<Activity> children = new ArrayList<>();
            for (ProcessElementDTO childDto : dto.getChildren()) {
                Activity child = toEntity(childDto);
                child.setSuperActivity(entity);
                children.add(child);
            }
            entity.setChildren(children);
        }
        return entity;
    }

    private Activity createProcessElementByType(ProcessType type) {
        Activity element;
        switch (type) {
            case ACTIVITY:
                element = new Activity();
                break;
            case TASK_DESCRIPTOR:
                element = new TaskDescriptor();
                break;
            case MILESTONE:
                element = new Milestone();
                break;
            case PHASE:
                element = new Phase();
                break;
            case ITERATION:
                element = new Iteration();
                break;
            default:
                throw new IllegalArgumentException("Tipo de elemento não suportado: " + type);
        }
        element.setIndex(currentIndex++);
        element.setType(type);
        return element;
    }

    private MethodElement toMethodEntity(MethodElementDTO dto) {
        MethodElement element;

        switch (dto.getType()) {
            case WORKPRODUCT:
                element = new WorkProduct();
                break;
            case ROLE:
                element = new Role();
                break;
            default:
                throw new IllegalArgumentException("Tipo de método não suportado: " + dto.getType());
        }

        element.setName(dto.getName());
        element.setModelInfo(dto.getModelInfo());
        element.optional();

        // Associa à atividade pai, se fornecido
        if (dto.getParentIndex() != null) {
            Activity parent = indexToActivity.get(dto.getParentIndex());
            if (parent != null) {
                element.setParentActivity(parent);
            }
        }

        return element;
    }

    @Transactional
    public Activity updateGenericActivity(Long id, ProcessElementDTO dto) {
        Activity activity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Elemento não encontrado com id: " + id));

        // Atualiza somente os campos permitidos
        if (dto.getName() != null) activity.setName(dto.getName());
        if (dto.getPredecessors() != null) activity.setPredecessors(dto.getPredecessors());
        activity.optional();

        return repository.save(activity);
    }

    @Transactional
    public MethodElement updateGenericMethod(Long id, MethodElementDTO dto) {
        MethodElement element = methodElementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Elemento não encontrado com id: " + id));

        if (dto.getName() != null) element.setName(dto.getName());
        if (dto.getModelInfo() != null) element.setModelInfo(dto.getModelInfo());
        element.optional();

        return methodElementRepository.save(element);
    }

    public void deleteElementById(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else if (methodElementRepository.existsById(id)) {
            methodElementRepository.deleteById(id);
        } else {
            throw new RuntimeException("Elemento com ID " + id + " não encontrado.");
        }
    }


    public List<Activity> getAllProcesses() {
        return repository.findAll();
    }
}


