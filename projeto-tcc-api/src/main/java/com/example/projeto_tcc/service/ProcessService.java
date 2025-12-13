package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.*;
import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.entity.Process;
import com.example.projeto_tcc.enums.ProcessType;
import com.example.projeto_tcc.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProcessService {

    private final ActivityRepository repository;
    private final MethodElementRepository methodElementRepository;
    private final WorkProductConfigService workProductConfigService;
    private final RoleConfigService roleConfigService;
    private final ActivityConfigService activityConfigService;
    private final WorkProductConfigRepository workProductConfigRepository;
    private final RoleConfigRepository roleConfigRepository;
    private final GeneratorConfigRepository generatorConfigRepository;
    private final ActivityConfigRepository activityConfigRepository;

    public ProcessService(ActivityRepository repository, MethodElementRepository methodElementRepository,
                          WorkProductConfigService workProductConfigService, RoleConfigService roleConfigService,
                          ActivityConfigService activityConfigService, WorkProductConfigRepository workProductConfigRepository,
                          RoleConfigRepository roleConfigRepository, GeneratorConfigRepository generatorConfigRepository,
                          ActivityConfigRepository activityConfigRepository) {
        this.repository = repository;
        this.methodElementRepository = methodElementRepository;
        this.workProductConfigService = workProductConfigService;
        this.roleConfigService = roleConfigService;
        this.activityConfigService = activityConfigService;
        this.workProductConfigRepository = workProductConfigRepository;
        this.roleConfigRepository = roleConfigRepository;
        this.generatorConfigRepository = generatorConfigRepository;
        this.activityConfigRepository = activityConfigRepository;
    }

    private int currentIndex = 0;
    private final Map<Integer, Activity> indexToActivity = new HashMap<>();

    // ------------------------------------------------------------------------
    // SAVE PROCESS
    // ------------------------------------------------------------------------

    public Process saveProcess(ProcessDTO dto) {
        currentIndex = 0;
        indexToActivity.clear();

        DeliveryProcess deliveryProcess = new DeliveryProcess();
        deliveryProcess.setName(dto.getName());
        deliveryProcess.setType(ProcessType.DELIVERY_PROCESS);
        deliveryProcess.setIndex(currentIndex++);
        deliveryProcess.setOptional(dto.isOptional());
        indexToActivity.put(deliveryProcess.getIndex(), deliveryProcess);
        deliveryProcess = repository.save(deliveryProcess);

        WorkBreakdownStructure wbs = new WorkBreakdownStructure();
        List<Activity> elements = new ArrayList<>();
        List<ProcessElementDTO> elementDTOs = dto.getProcessElements();

        for (ProcessElementDTO elemDto : elementDTOs) {
            Activity element = toEntityWithoutPredecessors(elemDto);
            elements.add(element);
        }

        for (int i = 0; i < elementDTOs.size(); i++) {
            resolvePredecessors(elementDTOs.get(i), elements.get(i));
        }

        if (dto.getPredecessors() != null) {
            List<Activity> resolvedPredecessors = new ArrayList<>();
            for (Integer predIndex : dto.getPredecessors()) {
                Activity pred = indexToActivity.get(predIndex);
                if (pred != null) {
                    resolvedPredecessors.add(pred);
                } else {
                    System.err.println("WARNING: PROCESS predecessor (save) with index " + predIndex + " not found.");
                }
            }
            deliveryProcess.setPredecessors(resolvedPredecessors);
        }

        elements = repository.saveAll(elements);

        for (Activity activity : elements) {
            activityConfigService.createDefaultConfigsRecursively(activity, deliveryProcess);
        }

        for (Activity activity : elements) {
            switch (activity.getType()) {
                case PHASE -> {
                    PhaseConfig phaseConfig = new PhaseConfig();
                    phaseConfig.setDeliveryProcess(deliveryProcess);
                    deliveryProcess.getPhaseConfigs().add(phaseConfig);
                }
                case ITERATION -> {
                    GeneratorConfig genConfig = new GeneratorConfig();
                    genConfig.setDeliveryProcess(deliveryProcess);
                    deliveryProcess.getGeneratorConfigs().add(genConfig);
                }
                case ACTIVITY -> {
                    ActivityConfig activityConfig = new ActivityConfig();
                    activityConfig.setDeliveryProcess(deliveryProcess);
                    deliveryProcess.getActivityConfigs().add(activityConfig);
                }
            }
        }

        wbs.setProcessElements(elements);

        List<MethodElement> methodElements = new ArrayList<>();
        if (dto.getMethodElements() != null) {
            for (MethodElementDTO methodDto : dto.getMethodElements()) {
                MethodElement method = toMethodEntity(methodDto);
                methodElements.add(method);
            }
        }

        wbs.setMethodElements(methodElements);
        deliveryProcess.setWbs(wbs);

        workProductConfigService.generateConfigurations(methodElements, elements, deliveryProcess);
        roleConfigService.generateConfigurations(methodElements, deliveryProcess);

        return repository.save(deliveryProcess);
    }

    // ------------------------------------------------------------------------
    // UPDATE PROCESS
    // ------------------------------------------------------------------------
    @Transactional
    public Process updateProcess(Long processId, ProcessDTO dto) {
        DeliveryProcess existingProcess = (DeliveryProcess) repository.findById(processId)
                .filter(p -> p instanceof DeliveryProcess)
                .orElseThrow(() -> new EntityNotFoundException("DeliveryProcess not found with ID: " + processId));

        existingProcess.setName(dto.getName());
        existingProcess.setOptional(dto.isOptional());

        workProductConfigRepository.deleteByDeliveryProcessId(processId);
        roleConfigRepository.deleteByDeliveryProcessId(processId);
        generatorConfigRepository.deleteByDeliveryProcessId(processId);

        WorkBreakdownStructure oldWbs = existingProcess.getWbs();
        if (oldWbs != null) {

            List<Activity> oldRootActivities = oldWbs.getProcessElements() != null
                    ? new ArrayList<>(oldWbs.getProcessElements())
                    : new ArrayList<>();

            List<Activity> allOldActivities = new ArrayList<>();
            collectAllActivities(oldRootActivities, allOldActivities);

            if (!allOldActivities.isEmpty()) {
                for (Activity act : allOldActivities) {
                    act.setPredecessors(new ArrayList<>());
                }
                repository.saveAllAndFlush(allOldActivities);
            }

            if (!allOldActivities.isEmpty()) {
                List<Long> oldIds = allOldActivities.stream()
                        .map(Activity::getId)
                        .collect(Collectors.toList());
                activityConfigRepository.deleteByActivityIdIn(oldIds);
                activityConfigRepository.flush();
            }

            existingProcess.setWbs(null);
        }

        repository.saveAndFlush(existingProcess);

        // RECONSTRUÇÃO DO PROCESSO
        currentIndex = 0;
        indexToActivity.clear();
        existingProcess.setIndex(currentIndex++);
        indexToActivity.put(existingProcess.getIndex(), existingProcess);

        WorkBreakdownStructure newWbs = new WorkBreakdownStructure();

        List<Activity> newRootElements = new ArrayList<>();
        List<ProcessElementDTO> elementDTOs = dto.getProcessElements();

        if (elementDTOs != null) {
            for (ProcessElementDTO elemDto : elementDTOs) {
                Activity element = toEntityWithoutPredecessors(elemDto);
                newRootElements.add(element);
            }

            for (int i = 0; i < elementDTOs.size(); i++) {
                resolvePredecessors(elementDTOs.get(i), newRootElements.get(i));
            }
        }

        // PREDECESSORES DO PROCESSO
        if (dto.getPredecessors() != null) {
            List<Activity> preds = new ArrayList<>();
            for (Integer predIndex : dto.getPredecessors()) {
                Activity pred = indexToActivity.get(predIndex);
                if (pred != null) preds.add(pred);
                else System.err.println("WARNING: Predecessor (update) with index " + predIndex + " not found.");
            }
            existingProcess.setPredecessors(preds);
        } else {
            existingProcess.setPredecessors(new ArrayList<>());
        }

        // METHOD ELEMENTS
        List<MethodElement> newMethodElements = new ArrayList<>();
        if (dto.getMethodElements() != null) {
            for (MethodElementDTO methodDto : dto.getMethodElements()) {
                newMethodElements.add(toMethodEntity(methodDto));
            }
        }
        newMethodElements = methodElementRepository.saveAll(newMethodElements);

        newWbs.setProcessElements(newRootElements);
        newWbs.setMethodElements(newMethodElements);
        existingProcess.setWbs(newWbs);

        // CONFIG DEFAULT PARA TODAS AS ATIVIDADES
        List<Activity> allNewActivities = new ArrayList<>();
        collectAllActivities(newRootElements, allNewActivities);

        for (Activity activity : allNewActivities) {
            activityConfigService.createDefaultConfigsRecursively(activity, existingProcess);
        }

        workProductConfigService.generateConfigurations(newMethodElements, newRootElements, existingProcess);
        roleConfigService.generateConfigurations(newMethodElements, existingProcess);

        return repository.save(existingProcess);
    }

    // ------------------------------------------------------------------------
    // DELETE PROCESS
    // ------------------------------------------------------------------------
    @Transactional
    public void deleteProcess(Long processId) {
        DeliveryProcess processToDelete = (DeliveryProcess) repository.findById(processId)
                .filter(p -> p instanceof DeliveryProcess)
                .orElseThrow(() -> new EntityNotFoundException("DeliveryProcess not found with ID: " + processId));

        workProductConfigRepository.deleteByDeliveryProcessId(processId);
        roleConfigRepository.deleteByDeliveryProcessId(processId);
        generatorConfigRepository.deleteByDeliveryProcessId(processId);

        WorkBreakdownStructure oldWbs = processToDelete.getWbs();
        if (oldWbs != null) {

            List<Activity> oldRootActivities = oldWbs.getProcessElements() != null
                    ? new ArrayList<>(oldWbs.getProcessElements())
                    : new ArrayList<>();

            List<Activity> allOldActivities = new ArrayList<>();
            collectAllActivities(oldRootActivities, allOldActivities);

            if (!allOldActivities.isEmpty()) {
                for (Activity act : allOldActivities) {
                    act.setPredecessors(new ArrayList<>());
                }
                repository.saveAllAndFlush(allOldActivities);
            }

            if (!allOldActivities.isEmpty()) {
                List<Long> ids = allOldActivities.stream().map(Activity::getId).collect(Collectors.toList());
                activityConfigRepository.deleteByActivityIdIn(ids);
                activityConfigRepository.flush();
            }

            processToDelete.setWbs(null);
            repository.saveAndFlush(processToDelete);
        }

        repository.delete(processToDelete);
    }

    // ------------------------------------------------------------------------
    // HELPERS
    // ------------------------------------------------------------------------

    private void collectAllActivities(List<Activity> roots, List<Activity> result) {
        if (roots == null) return;
        for (Activity activity : roots) {
            if (activity != null) {
                result.add(activity);
                collectAllActivities(activity.getChildren(), result);
            }
        }
    }

    private Activity toEntityWithoutPredecessors(ProcessElementDTO dto) {
        Activity entity = createProcessElementByType(dto.getType());
        entity.setName(dto.getName());
        entity.setOptional(dto.isOptional());

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
            List<Activity> resolved = new ArrayList<>();
            for (Integer predIndex : dto.getPredecessors()) {
                Activity pred = indexToActivity.get(predIndex);
                if (pred != null) resolved.add(pred);
                else System.err.println("WARNING: Predecessor index " + predIndex + " not found.");
            }
            entity.setPredecessors(resolved);
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
        element.setOptional(dto.isOptional());
        element.setMethodType(dto.getType());

        if (dto.getParentIndex() != null) {
            Activity parent = indexToActivity.get(dto.getParentIndex());
            if (parent != null) element.setParentActivity(parent);
        }

        return element;
    }

    // ------------------------------------------------------------------------
    // UPDATE ELEMENTS
    // ------------------------------------------------------------------------

    @Transactional
    public Activity updateGenericActivity(Long id, ProcessElementDTO dto) {
        Activity activity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Element not found with id: " + id));

        if (dto.getName() != null) activity.setName(dto.getName());
        activity.setOptional(dto.isOptional());

        return repository.save(activity);
    }

    @Transactional
    public MethodElement updateGenericMethod(Long id, MethodElementDTO dto) {
        MethodElement element = methodElementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Element not found with id: " + id));

        if (dto.getName() != null) element.setName(dto.getName());
        if (dto.getModelInfo() != null) element.setModelInfo(dto.getModelInfo());
        element.setOptional(dto.isOptional());

        return methodElementRepository.save(element);
    }

    public void deleteElementById(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else if (methodElementRepository.existsById(id)) {
            methodElementRepository.deleteById(id);
        } else {
            throw new RuntimeException("Element with ID " + id + " not found.");
        }
    }

    // ------------------------------------------------------------------------
    // DTO CONVERSION
    // ------------------------------------------------------------------------

    public ProcessGetDTO convertToGetDTO(Activity entity) {
        ProcessGetDTO dto = new ProcessGetDTO();
        dto.setName(entity.getName());
        dto.setIndex(entity.getIndex());
        dto.setModelInfo(entity.getModelInfo());
        dto.setType(entity.getType());
        dto.setPredecessors(entity.getPredecessors());
        dto.setOptional(entity.isOptional());

        if (entity instanceof DeliveryProcess dp) {
            WorkBreakdownStructure wbs = dp.getWbs();
            if (wbs != null) {
                if (wbs.getProcessElements() != null) {
                    List<ProcessElementDTO> list =
                            wbs.getProcessElements().stream()
                                    .map(this::convertToProcessElementDTO)
                                    .toList();
                    dto.setProcessElements(list);
                }

                if (wbs.getMethodElements() != null) {
                    List<GetMethodElementDTO> list =
                            wbs.getMethodElements().stream()
                                    .map(this::convertToGetMethodDTO)
                                    .toList();
                    dto.setMethodElements(list);
                }
            }
        }

        return dto;
    }

    public GetMethodElementDTO convertToGetMethodDTO(MethodElement method) {
        GetMethodElementDTO dto = new GetMethodElementDTO();
        dto.setName(method.getName());
        dto.setModelInfo(method.getModelInfo());
        dto.setParentIndex(method.getParentActivity() != null ? method.getParentActivity().getIndex() : null);
        dto.setOptional(method.isOptional());
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
            List<ProcessElementDTO> list =
                    entity.getChildren().stream()
                            .map(this::convertToProcessElementDTO)
                            .toList();
            dto.setChildren(list);
        }

        dto.setOptional(entity.isOptional());
        return dto;
    }
}
