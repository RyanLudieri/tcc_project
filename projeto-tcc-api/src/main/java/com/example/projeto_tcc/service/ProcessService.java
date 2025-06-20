package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.MethodElementDTO;
import com.example.projeto_tcc.dto.ProcessDTO;
import com.example.projeto_tcc.dto.ProcessElementDTO;
import com.example.projeto_tcc.dto.ProcessGetDTO;
import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.entity.Process;
import com.example.projeto_tcc.enums.ProcessType;
import com.example.projeto_tcc.repository.MethodElementRepository;
import com.example.projeto_tcc.repository.ActivityRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProcessService {

    private final ActivityRepository repository;
    private final MethodElementRepository methodElementRepository;

    public ProcessService(ActivityRepository repository, MethodElementRepository methodElementRepository) {
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

        List<Activity> elements = new ArrayList<>();
        List<ProcessElementDTO> elementDTOs = dto.getProcessElements();

    // Etapa 1: cria as atividades sem predecessores
        for (ProcessElementDTO elemDto : elementDTOs) {
            Activity element = toEntityWithoutPredecessors(elemDto);
            elements.add(element);
        }

    // Etapa 2: resolve os predecessores
        for (int i = 0; i < elementDTOs.size(); i++) {
            resolvePredecessors(elementDTOs.get(i), elements.get(i));
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



    // Cria a árvore de atividades SEM setar predecessores
    private Activity toEntityWithoutPredecessors(ProcessElementDTO dto) {
        Activity entity = createProcessElementByType(dto.getType());
        entity.setName(dto.getName());
        entity.optional();

        indexToActivity.put(entity.getIndex(), entity);

        if (dto.getChildren() != null) {
            List<Activity> children = new ArrayList<>();
            for (ProcessElementDTO childDto : dto.getChildren()) {
                Activity child = toEntityWithoutPredecessors(childDto);
                child.setSuperActivity(entity);
                children.add(child);
            }
            entity.setChildren(children);
        }
        return entity;
    }

    private void resolvePredecessors(ProcessElementDTO dto, Activity entity) {
        if (dto.getPredecessors() != null) {
            List<Activity> resolvedPredecessors = new ArrayList<>();
            for (Integer predIndex : dto.getPredecessors()) {
                Activity pred = indexToActivity.get(predIndex);
                if (pred != null) {
                    resolvedPredecessors.add(pred);
                } else {
                    System.err.println("WARNING: Predecessor with index " + predIndex + " not found.");
                }
            }
            entity.setPredecessors(resolvedPredecessors);
        }

        if (dto.getChildren() != null && entity.getChildren() != null) {
            for (int i = 0; i < dto.getChildren().size(); i++) {
                resolvePredecessors(dto.getChildren().get(i), entity.getChildren().get(i));
            }
        }
    }


    private Activity createProcessElementByType(ProcessType type) {
        Activity element = type.createInstance();
        element.setIndex(currentIndex++);
        element.setType(type);
        return element;
    }


    private MethodElement toMethodEntity(MethodElementDTO dto) {
        MethodElement element = dto.getType().createInstance();

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

    public ProcessGetDTO convertToGetDTO(Activity entity) {
        ProcessGetDTO dto = new ProcessGetDTO();
        dto.setName(entity.getName());
        dto.setIndex(entity.getIndex());
        dto.setModelInfo(entity.getModelInfo());
        dto.setType(entity.getType());
        dto.setPredecessors(entity.getPredecessors());

        // Se DeliveryProcess tiver WBS e ProcessElements:
        if (entity instanceof DeliveryProcess) {
            DeliveryProcess dp = (DeliveryProcess) entity;
            if (dp.getWbs() != null) {
                List<Activity> elements = dp.getWbs().getProcessElements();
                List<ProcessElementDTO> elementDTOs = elements.stream()
                        .map(this::convertToProcessElementDTO)
                        .toList();
                dto.setProcessElements(elementDTOs);
            }
        }

        return dto;
    }

    private ProcessElementDTO convertToProcessElementDTO(Activity entity) {
        ProcessElementDTO dto = new ProcessElementDTO();
        dto.setName(entity.getName());
        dto.setIndex(entity.getIndex());
        dto.setModelInfo(entity.getModelInfo());
        dto.setType(entity.getType());
        dto.setPredecessors(entity.getPredecessors() == null ? null :
                entity.getPredecessors().stream().map(Activity::getIndex).toList());
        if (entity.getChildren() != null) {
            List<ProcessElementDTO> childrenDto = entity.getChildren().stream()
                    .map(this::convertToProcessElementDTO)
                    .toList();
            dto.setChildren(childrenDto);
        }
        return dto;
    }

}


