package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.MethodElementObserverDTO;
import com.example.projeto_tcc.dto.ObserverUpdateDTO;
import com.example.projeto_tcc.dto.WorkProductConfigDTO;
import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.entity.Observer;
import com.example.projeto_tcc.enums.ObserverMethodElementType;
import com.example.projeto_tcc.enums.ProcessType;
import com.example.projeto_tcc.enums.Queue;
import com.example.projeto_tcc.repository.MethodElementRepository;
import com.example.projeto_tcc.repository.MethodElementObserverRepository;
import com.example.projeto_tcc.repository.WorkProductConfigRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkProductConfigService {

    private final WorkProductConfigRepository configRepository;
    private final MethodElementObserverRepository observerRepository;
    private final MethodElementRepository methodElementRepository;

    private int queueIndex = 0;
    private final Map<String, WorkProductConfig> queueMap = new LinkedHashMap<>();

    @Transactional
    public void generateConfigurations(List<MethodElement> methodElements, List<Activity> roots, DeliveryProcess deliveryProcess) {
        queueIndex = 0;
        queueMap.clear();

        Set<String> activeWorkProducts = new LinkedHashSet<>();

        for (Activity root : roots) {
            traverseAndCreateConfigs(root, deliveryProcess, activeWorkProducts);
        }

        // --- INÍCIO DA LÓGICA DE OUTPUT FINAL UNIFICADA E CORRETA ---
        if (roots != null && !roots.isEmpty()) {
            // A lógica é baseada no TIPO da última atividade raiz do processo.
            Activity lastRoot = roots.get(roots.size() - 1);

            String finalEventName;
            Set<String> finalOutputWPs;

            if (isTaskLike(lastRoot)) {
                // CASO 1: Se a última raiz for uma tarefa, o output é específico dela.
                // (Resolve o caso do "Inquirer")
                finalEventName = lastRoot.getName();
                finalOutputWPs = getAssociatedWorkProductNames(lastRoot, "OUTPUT");

            } else {
                // CASO 2: Se a última raiz for um Contêiner ou Milestone.
                // (Resolve os casos da "Iteration A" e "Working software")
                finalEventName = (lastRoot.getType() == ProcessType.MILESTONE) ? lastRoot.getName() : "END_" + lastRoot.getName();
                finalOutputWPs = new LinkedHashSet<>(activeWorkProducts);
            }

            // Cria a configuração de OUTPUT associada à atividade raiz.
            createConfigsIfAbsent(lastRoot, finalOutputWPs, finalEventName, "OUTPUT", deliveryProcess);
        }
    }

    private void traverseAndCreateConfigs(Activity activity, DeliveryProcess deliveryProcess, Set<String> activeWorkProducts) {
        if (isTaskLike(activity)) {
            Set<String> inputWPs = getAssociatedWorkProductNames(activity, "INPUT");
            createConfigsIfAbsent(activity, inputWPs, activity.getName(), "INPUT", deliveryProcess);

            Set<String> outputWPs = getAssociatedWorkProductNames(activity, "OUTPUT");
            activeWorkProducts.addAll(outputWPs);

        } else if (activity.getType() == ProcessType.MILESTONE) {
            createConfigsIfAbsent(activity, new LinkedHashSet<>(activeWorkProducts), activity.getName(), "INPUT", deliveryProcess);

        } else { // É um Container
            Set<String> wpsInThisScope = getAllWorkProductsInScope(activity);
            createConfigsIfAbsent(activity, wpsInThisScope, "BEGIN_" + activity.getName(), "INPUT", deliveryProcess);

            Set<String> activeWPsForChildren = new LinkedHashSet<>(activeWorkProducts);
            if (activity.getChildren() != null) {
                for (Object childObj : activity.getChildren()) {
                    if (childObj instanceof Activity) {
                        traverseAndCreateConfigs((Activity) childObj, deliveryProcess, activeWPsForChildren);
                    }
                }
            }
            activeWorkProducts.addAll(activeWPsForChildren);

            createConfigsIfAbsent(activity, wpsInThisScope, "END_" + activity.getName(), "INPUT", deliveryProcess);
        }
    }

    private boolean isTaskLike(Activity activity) {
        ProcessType type = activity.getType();
        return type == ProcessType.TASK_DESCRIPTOR ||
                (type == ProcessType.ACTIVITY && (activity.getChildren() == null || activity.getChildren().isEmpty()));
    }

    private Activity findLastTask(Activity activity) {
        if (activity.getChildren() == null || activity.getChildren().isEmpty()) {
            return activity;
        }

        List<Activity> children = new ArrayList<>();
        for (Object childObj : activity.getChildren()) {
            if (childObj instanceof Activity) {
                children.add((Activity) childObj);
            }
        }

        if (children.isEmpty()) {
            return activity;
        }
        return findLastTask(children.get(children.size() - 1));
    }

    private Set<String> getAssociatedWorkProductNames(Activity activity, String type) {
        if (activity == null) return Collections.emptySet();
        String modelInfoToFind = type.equals("INPUT") ? "MANDATORY_INPUT" : "OUTPUT";
        List<MethodElement> associatedElements = methodElementRepository.findByParentActivity(activity);

        return associatedElements.stream()
                .filter(me -> me.getModelInfo() != null && me.getModelInfo().equals(modelInfoToFind))
                .map(MethodElement::getName)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<String> getAllWorkProductsInScope(Activity container) {
        Set<String> workProducts = new LinkedHashSet<>();
        if (container == null) return workProducts;
        collectWorkProductsRecursively(container, workProducts);
        return workProducts;
    }

    private void collectWorkProductsRecursively(Activity currentActivity, Set<String> collectedWorkProducts) {
        collectedWorkProducts.addAll(getAssociatedWorkProductNames(currentActivity, "INPUT"));
        collectedWorkProducts.addAll(getAssociatedWorkProductNames(currentActivity, "OUTPUT"));

        if (currentActivity.getChildren() != null) {
            for (Object childObj : currentActivity.getChildren()) {
                if (childObj instanceof Activity) {
                    collectWorkProductsRecursively((Activity) childObj, collectedWorkProducts);
                }
            }
        }
    }

    private void createConfigsIfAbsent(Activity activity, Set<String> wpNames, String taskName, String inputOutput, DeliveryProcess deliveryProcess) {
        if (wpNames == null || wpNames.isEmpty()) return;

        for (String wpName : wpNames) {
            String key = wpName + "|" + taskName + "|" + inputOutput;
            if (queueMap.containsKey(key)) continue;

            WorkProductConfig config = new WorkProductConfig();
            config.setActivity(activity);
            config.setWorkProductName(wpName);
            config.setQueue_name("q" + queueIndex++);
            config.setQueue_type("QUEUE");
            config.setQueue_size(50);
            config.setInitial_quantity(0);
            config.setPolicy(Queue.FIFO);
            config.setGenerate_activity(false);
            config.setInput_output(inputOutput);
            config.setTask_name(taskName);
            config.setDeliveryProcess(deliveryProcess);

            configRepository.save(config);
            queueMap.put(key, config);

            createDefaultObserver(config);
        }
    }

    private void createDefaultObserver(WorkProductConfig config) {
        MethodElementObserver observer = new MethodElementObserver();
        observer.setPosition(1);
        observer.setName(config.getQueue_name() + " Observer " + queueIndex);
        observer.setQueue_name(config.getQueue_name());
        observer.setType(ObserverMethodElementType.LENGTH);
        observer.setWorkProductConfig(config);
        observerRepository.save(observer);
    }

    @Transactional
    public List<WorkProductConfigDTO> getWorkProductsByDeliveryProcess(Long deliveryProcessId) {
        List<WorkProductConfig> workProducts = configRepository.findByDeliveryProcessId(deliveryProcessId);
        workProducts.forEach(wp -> wp.getObservers().size());

        return workProducts.stream()
                .sorted(Comparator.comparingInt(wp -> Integer.parseInt(wp.getQueue_name().substring(1))))
                .map(wp -> new WorkProductConfigDTO(
                        wp.getId(),
                        wp.getWorkProductName(),
                        wp.getInput_output(),
                        wp.getTask_name(),
                        wp.getQueue_name(),
                        wp.getQueue_type(),
                        wp.getQueue_size(),
                        wp.getInitial_quantity(),
                        wp.getPolicy(),
                        wp.isGenerate_activity(),
                        wp.getActivity() != null ? wp.getActivity().getId() : null,
                        wp.getObservers().stream()
                                .map(obs -> new MethodElementObserverDTO(
                                        obs.getId(),
                                        obs.getQueue_name(),
                                        obs.getName(),
                                        obs.getPosition(),
                                        obs.getType(),
                                        obs.getWorkProductConfig() != null ? obs.getWorkProductConfig().getId() : null
                                ))
                                .toList()
                ))
                .toList();
    }

    @Transactional
    public MethodElementObserver addObserverToWorkProductConfig(Long workProductConfigId, ObserverMethodElementType type) {
        WorkProductConfig config = configRepository.findById(workProductConfigId)
                .orElseThrow(() -> new IllegalArgumentException("WorkProductConfig não encontrado"));

        int nextPosition = config.getObservers().stream()
                .mapToInt(Observer::getPosition)
                .max()
                .orElse(0) + 1;

        MethodElementObserver observer = new MethodElementObserver();
        observer.setPosition(nextPosition);
        observer.setQueue_name(config.getQueue_name());
        observer.setName(config.getQueue_name() + " Observer " + nextPosition);
        observer.setType(type != null ? type : ObserverMethodElementType.NONE);
        observer.setWorkProductConfig(config);

        config.getObservers().add(observer);
        return observerRepository.save(observer);
    }

    @Transactional
    public void removeObserverFromWorkProductConfig(Long workProductConfigId, Long observerId) {
        WorkProductConfig config = configRepository.findById(workProductConfigId)
                .orElseThrow(() -> new EntityNotFoundException("WorkProductConfig not found with ID: " + workProductConfigId));

        MethodElementObserver observerToRemove = config.getObservers().stream()
                .filter(obs -> obs.getId().equals(observerId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Observer not found with ID: " + observerId));

        config.getObservers().remove(observerToRemove);
        observerRepository.delete(observerToRemove);
        configRepository.save(config);
    }

    @Transactional
    public MethodElementObserver updateObserver(Long observerId, ObserverUpdateDTO dto) {
        MethodElementObserver observer = observerRepository.findById(observerId)
                .orElseThrow(() -> new IllegalArgumentException("Observer não encontrado com id: " + observerId));

        if (dto.getType() != null) observer.setType(dto.getType());
        if (dto.getQueueName() != null) observer.setQueue_name(dto.getQueueName());

        return observerRepository.save(observer);
    }

    @Transactional
    public WorkProductConfigDTO updateWorkProductConfig(Long workProductConfigId, WorkProductConfigDTO dto) {
        WorkProductConfig config = configRepository.findById(workProductConfigId)
                .orElseThrow(() -> new IllegalArgumentException("WorkProductConfig não encontrado"));

        if (dto.getQueue_name() != null) config.setQueue_name(dto.getQueue_name());
        if (dto.getQueue_type() != null) config.setQueue_type(dto.getQueue_type());
        if (dto.getQueue_size() != null) config.setQueue_size(dto.getQueue_size());
        if (dto.getInitial_quantity() != null) config.setInitial_quantity(dto.getInitial_quantity());
        if (dto.getPolicy() != null) config.setPolicy(dto.getPolicy());
        config.setGenerate_activity(dto.isGenerate_activity());

        WorkProductConfig saved = configRepository.save(config);

        return new WorkProductConfigDTO(
                saved.getId(),
                saved.getWorkProductName(),
                saved.getInput_output(),
                saved.getTask_name(),
                saved.getQueue_name(),
                saved.getQueue_type(),
                saved.getQueue_size(),
                saved.getInitial_quantity(),
                saved.getPolicy(),
                saved.isGenerate_activity(),
                saved.getActivity() != null ? saved.getActivity().getId() : null,
                saved.getObservers().stream()
                        .map(obs -> new MethodElementObserverDTO(
                                obs.getId(),
                                obs.getQueue_name(),
                                obs.getName(),
                                obs.getPosition(),
                                obs.getType(),
                                obs.getWorkProductConfig() != null ? obs.getWorkProductConfig().getId() : null
                        ))
                        .toList()
        );
    }
}