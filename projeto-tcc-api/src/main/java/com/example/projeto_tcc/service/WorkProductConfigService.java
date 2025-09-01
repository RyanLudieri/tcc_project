package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.MethodElementObserverDTO;
import com.example.projeto_tcc.dto.ObserverUpdateDTO;
import com.example.projeto_tcc.dto.WorkProductConfigDTO;
import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.entity.Observer;
import com.example.projeto_tcc.enums.ObserverMethodElementType;
import com.example.projeto_tcc.enums.ProcessType;
import com.example.projeto_tcc.enums.Queue;
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

    // índice global das filas (q0, q1, ...)
    private int queueIndex = 0;

    // mapa para evitar duplicatas: key = wpName + "|" + taskName + "|" + inputOutput
    private final Map<String, WorkProductConfig> queueMap = new LinkedHashMap<>();

    @Transactional
    public void generateConfigurations(List<MethodElement> methodElements, List<Activity> roots, DeliveryProcess deliveryProcess) {
        // Preserva ordem de aparição dos work products (determinístico)
        Set<String> uniqueWPNames = methodElements.stream()
                .filter(me -> me.getModelInfo() != null &&
                        (me.getModelInfo().equals("MANDATORY_INPUT") || me.getModelInfo().equals("OUTPUT")))
                .map(MethodElement::getName)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Reset de estado
        queueIndex = 0;
        queueMap.clear();

        // 1. Executa a travessia normal, onde todos os eventos de ligação são "INPUT"
        for (Activity root : roots) {
            traverseAndCreateConfigs(root, uniqueWPNames, deliveryProcess);
        }

        // 2. Adiciona o evento OUTPUT extra apenas para a última atividade de todo o processo
        if (roots != null && !roots.isEmpty()) {
            Activity lastRoot = roots.get(roots.size() - 1);

            String finalTaskName;
            ProcessType type = lastRoot.getType();

            // Determina o nome correto para a "tarefa" de output final
            if (type == ProcessType.TASK_DESCRIPTOR || type == ProcessType.MILESTONE) {
                finalTaskName = lastRoot.getName();
            } else {
                // Para nós internos, o evento de finalização correspondente é "END_<nome>"
                finalTaskName = "END_" + lastRoot.getName();
            }

            // Cria a configuração final e especial de OUTPUT
            createConfigsIfAbsent(lastRoot, uniqueWPNames, finalTaskName, "OUTPUT", deliveryProcess);
        }
    }

    /**
     * Implementa a travessia recursiva para gerar a cadeia de eventos de INPUT.
     * - TASK (folha)         -> criar INPUT (apenas)
     * - MILESTONE (folha)    -> criar INPUT (antes) e INPUT (depois)
     * - Nó Interno           -> criar BEGIN_<nome> (INPUT) ; recursão nos filhos ; criar END_<nome> (INPUT)
     */
    private void traverseAndCreateConfigs(Activity activity, Set<String> uniqueWPNames, DeliveryProcess deliveryProcess) {
        boolean isTask = activity.getType() == ProcessType.TASK_DESCRIPTOR;
        boolean isMilestone = activity.getType() == ProcessType.MILESTONE;

        if (isTask) {
            // Task: sempre gera apenas um INPUT
            createConfigsIfAbsent(activity, uniqueWPNames, activity.getName(), "INPUT", deliveryProcess);
            if (activity.getChildren() != null) {
                for (Activity child : activity.getChildren()) {
                    traverseAndCreateConfigs(child, uniqueWPNames, deliveryProcess);
                }
            }
            return;
        }

        if (isMilestone) {
            // Milestone intermediário: gera INPUT antes e INPUT depois para ligar com o próximo passo
            createConfigsIfAbsent(activity, uniqueWPNames, activity.getName(), "INPUT", deliveryProcess);
            if (activity.getChildren() != null) {
                for (Activity child : activity.getChildren()) {
                    traverseAndCreateConfigs(child, uniqueWPNames, deliveryProcess);
                }
            }
            createConfigsIfAbsent(activity, uniqueWPNames, activity.getName(), "INPUT", deliveryProcess); // Padronizado para INPUT
            return;
        }

        // Nó Interno: BEGIN (INPUT) -> filhos -> END (INPUT)
        createConfigsIfAbsent(activity, uniqueWPNames, "BEGIN_" + activity.getName(), "INPUT", deliveryProcess);

        if (activity.getChildren() != null) {
            for (Activity child : activity.getChildren()) {
                traverseAndCreateConfigs(child, uniqueWPNames, deliveryProcess);
            }
        }

        createConfigsIfAbsent(activity, uniqueWPNames, "END_" + activity.getName(), "INPUT", deliveryProcess);
    }

    private void createConfigsIfAbsent(Activity activity, Set<String> wpNames, String taskName, String inputOutput, DeliveryProcess deliveryProcess) {
        for (String wpName : wpNames) {
            String key = wpName + "|" + taskName + "|" + inputOutput;
            if (queueMap.containsKey(key)) {
                // Já existe essa fila para essa combinação — pula
                continue;
            }

            WorkProductConfig config = new WorkProductConfig();
            config.setActivity(activity);
            config.setWorkProductName(wpName);
            config.setQueue_name("q" + queueIndex++); // Só incrementa quando realmente cria a fila
            config.setQueue_type("QUEUE");
            config.setQueue_size(50);
            config.setInitial_quantity(0);
            config.setPolicy(Queue.FIFO);
            config.setGenerate_activity(false);
            config.setInput_output(inputOutput);
            config.setTask_name(taskName);

            // Associa ao DeliveryProcess
            config.setDeliveryProcess(deliveryProcess);

            configRepository.save(config);
            queueMap.put(key, config);

            // 🚀 cria observer padrão
            createDefaultObserver(config);
        }
    }

    //Metodo auxiliar para criar o observer padrão
    private void createDefaultObserver(WorkProductConfig config) {
        MethodElementObserver observer = new MethodElementObserver();
        observer.setPosition(1);
        observer.setName(config.getQueue_name() + " Observer " + queueIndex);
        observer.setQueue_name(config.getQueue_name());
        observer.setType(ObserverMethodElementType.LENGTH); // enum específico do seu domínio
        observer.setWorkProductConfig(config);

        observerRepository.save(observer);
    }


    //GET
    @Transactional
    public List<WorkProductConfigDTO> getWorkProductsByDeliveryProcess(Long deliveryProcessId) {
        List<WorkProductConfig> workProducts = configRepository.findByDeliveryProcessId(deliveryProcessId);

        // força carregar observers (LazyInitializationException)
        workProducts.forEach(wp -> wp.getObservers().size());

        return workProducts.stream()
                .sorted(Comparator.comparingInt(wp -> Integer.parseInt(wp.getQueue_name().substring(1)))) // ordena pelo número da fila
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




    // ADD OBSERVER
    @Transactional
    public MethodElementObserver addObserverToWorkProductConfig(Long workProductConfigId) {
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
        observer.setType(ObserverMethodElementType.LENGTH);
        observer.setWorkProductConfig(config);

        config.getObservers().add(observer);
        return observerRepository.save(observer);
    }


    // REMOVE OBSERVER
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

    // ---------------------------------------------------
    // UPDATE OBSERVER
    @Transactional
    public MethodElementObserver updateObserver(Long observerId, ObserverUpdateDTO dto) {
        MethodElementObserver observer = observerRepository.findById(observerId)
                .orElseThrow(() -> new IllegalArgumentException("Observer não encontrado com id: " + observerId));

        if (dto.getType() != null) {
            observer.setType(dto.getType());
        }
        if (dto.getQueueName() != null) {
            observer.setQueue_name(dto.getQueueName());
        }

        return observerRepository.save(observer);
    }

    // ---------------------------------------------------
    // UPDATE CONFIG
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

        // Retorna o DTO com apenas o activityId
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