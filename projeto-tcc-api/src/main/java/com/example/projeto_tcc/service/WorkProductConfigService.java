package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.*;
import com.example.projeto_tcc.entity.Observer;
import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.enums.ObserverMethodElementType;
import com.example.projeto_tcc.enums.ProcessType;
import com.example.projeto_tcc.enums.Queue;
import com.example.projeto_tcc.enums.VariableType;
import com.example.projeto_tcc.repository.*;
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
    private final DeliveryProcessRepository deliveryProcessRepository;
    private final GeneratorConfigRepository generatorConfigRepository;
    private final GeneratorObserverRepository generatorObserverRepository;
    private final WorkProductConfigRepository workProductConfigRepository;

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
            config.setDestroyer(false);
            config.setInput_output(inputOutput);
            config.setTask_name(taskName);
            config.setDeliveryProcess(deliveryProcess);
            config.setVariableType(VariableType.INDEPENDENT);

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
                        wp.isDestroyer(),
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

    public List<WorkProductConfigGetDTO> findAllWorkProductConfigs(Long processId) {
        List<WorkProductConfig> entities = configRepository.findAllByDeliveryProcessIdOrderByIdAsc(processId);

        return entities.stream()
                .map(this::convertToGetDTO)
                .collect(Collectors.toList());
    }

    private WorkProductConfigGetDTO convertToGetDTO(WorkProductConfig entity) {
        return new WorkProductConfigGetDTO(
                entity.getId(),
                entity.getWorkProductName(),
                entity.getInput_output(),
                entity.getTask_name(),
                entity.getQueue_name(),
                entity.getVariableType()
        );
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
    public WorkProductConfigUpdateDTO updateWorkProductConfig(Long workProductConfigId, WorkProductConfigUpdateDTO dto) {
        WorkProductConfig config = configRepository.findById(workProductConfigId)
                .orElseThrow(() -> new IllegalArgumentException("WorkProductConfig não encontrado"));

        if (dto.getQueue_name() != null) config.setQueue_name(dto.getQueue_name());
        if (dto.getQueue_type() != null) config.setQueue_type(dto.getQueue_type());
        if (dto.getQueue_size() != null) config.setQueue_size(dto.getQueue_size());
        if (dto.getInitial_quantity() != null) config.setInitial_quantity(dto.getInitial_quantity());
        if (dto.getPolicy() != null) config.setPolicy(dto.getPolicy());
        if(dto.getVariableType() != null) config.setVariableType(dto.getVariableType());
        config.setGenerate_activity(dto.isGenerate_activity());
        config.setDestroyer(dto.isDestroy());

        WorkProductConfig saved = configRepository.save(config);

        return new WorkProductConfigUpdateDTO(
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
                saved.isDestroyer(),
                saved.getVariableType(),
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


    @Transactional
    public GeneratorConfigDTO addGeneratorToProcess(Long processId, GeneratorConfigRequestDTO requestDto) {
        DeliveryProcess process = deliveryProcessRepository.findById(processId)
                .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado com ID: " + processId));
        WorkProductConfig targetQueue = configRepository.findById(requestDto.getWorkProductConfigId())
                .orElseThrow(() -> new EntityNotFoundException("WorkProductConfig de destino não encontrado com ID: " + requestDto.getWorkProductConfigId()));

        DistributionParameter dist = new DistributionParameter();
        dist.setConstant(requestDto.getConstant());
        dist.setMean(requestDto.getMean());
        dist.setAverage(requestDto.getAverage());
        dist.setStandardDeviation(requestDto.getStandardDeviation());
        dist.setLow(requestDto.getLow());
        dist.setHigh(requestDto.getHigh());
        dist.setScale(requestDto.getScale());
        dist.setShape(requestDto.getShape());

        GeneratorConfig newGenerator = new GeneratorConfig();
        newGenerator.setDistributionType(requestDto.getDistributionType());
        newGenerator.setDistribution(dist);
        newGenerator.setTargetWorkProduct(targetQueue);
        newGenerator.setDeliveryProcess(process);
        process.getGeneratorConfigs().add(newGenerator);

        targetQueue.setGenerate_activity(true);
        configRepository.save(targetQueue);

        GeneratorConfig savedGeneratorEntity = generatorConfigRepository.save(newGenerator);

        return toResponseDTO(savedGeneratorEntity);
    }

    @Transactional
    public GeneratorConfigDTO updateGenerator(Long generatorId, GeneratorConfigRequestDTO dto) {
        GeneratorConfig existingGenerator = generatorConfigRepository.findById(generatorId)
                .orElseThrow(() -> new EntityNotFoundException("Gerador não encontrado com ID: " + generatorId));

        existingGenerator.setDistributionType(dto.getDistributionType());

        DistributionParameter dist = existingGenerator.getDistribution();
        if (dist == null) {
            dist = new DistributionParameter();
            existingGenerator.setDistribution(dist);
        }

        dist.setConstant(dto.getConstant());
        dist.setMean(dto.getMean());
        dist.setAverage(dto.getAverage());
        dist.setStandardDeviation(dto.getStandardDeviation());
        dist.setLow(dto.getLow());
        dist.setHigh(dto.getHigh());
        dist.setScale(dto.getScale());
        dist.setShape(dto.getShape());

        GeneratorConfig savedGenerator = generatorConfigRepository.save(existingGenerator);

        return toResponseDTO(savedGenerator);
    }

    @Transactional
    public void removeGenerator(Long generatorId) {
        GeneratorConfig generator = generatorConfigRepository.findById(generatorId)
                .orElseThrow(() -> new EntityNotFoundException("Gerador não encontrado com ID: " + generatorId));

        WorkProductConfig target = generator.getTargetWorkProduct();
        if (target != null) {
            target.setGenerate_activity(false);
            configRepository.save(target);
        }

        DeliveryProcess process = generator.getDeliveryProcess();
        if (process != null) {
            process.getGeneratorConfigs().remove(generator);
            deliveryProcessRepository.save(process);
        } else {
            generatorConfigRepository.delete(generator);
        }
    }

    @Transactional
    public void setDestroyer(Long workProductConfigId, boolean isDestroyer) {
        WorkProductConfig selectedQueue = configRepository.findById(workProductConfigId)
                .orElseThrow(() -> new EntityNotFoundException("WorkProductConfig não encontrado"));
        selectedQueue.setDestroyer(isDestroyer);
        configRepository.save(selectedQueue);
    }

    private GeneratorConfigDTO toResponseDTO(GeneratorConfig entity) {
        if (entity == null) return null;

        DistributionParameterDTO distDto = null;
        if (entity.getDistribution() != null) {
            DistributionParameter dist = entity.getDistribution();
            distDto = new DistributionParameterDTO();
            distDto.setId(dist.getId());
            distDto.setConstant(dist.getConstant());
            distDto.setMean(dist.getMean());
            distDto.setAverage(dist.getAverage());
            distDto.setStandardDeviation(dist.getStandardDeviation());
            distDto.setLow(dist.getLow());
            distDto.setHigh(dist.getHigh());
            distDto.setScale(dist.getScale());
            distDto.setShape(dist.getShape());
        }

        WorkProductConfigSummaryDTO wpDto = null;
        if (entity.getTargetWorkProduct() != null) {
            WorkProductConfig wp = entity.getTargetWorkProduct();
            wpDto = new WorkProductConfigSummaryDTO(
                    wp.getId(),
                    wp.getWorkProductName(),
                    wp.getQueue_name(),
                    wp.isGenerate_activity()
            );
        }

        List<GenerateObserverDTO> observerDtos = entity.getObservers().stream()
                .map(this::mapObserverToDTO)
                .collect(Collectors.toList());

        return new GeneratorConfigDTO(
                entity.getId(),
                entity.getDistributionType(),
                distDto,
                wpDto,
                observerDtos
        );
    }

    private GenerateObserverDTO mapObserverToDTO(GeneratorObserver entity) {
        if (entity == null) return null;
        return new GenerateObserverDTO(
                entity.getId(),
                entity.getName(),
                entity.getQueue_name(),
                entity.getPosition(),
                entity.getType()
        );
    }

    @Transactional
    public List<GeneratorConfigDTO> getGeneratorsByProcess(Long processId) {
        // Validação se o processo existe pode ser adicionada aqui
        // deliveryProcessRepository.findById(processId).orElseThrow(...)

        List<GeneratorConfig> generators = generatorConfigRepository.findByDeliveryProcessId(processId);
        return generators.stream()
                .map(this::toResponseDTO) // Reutiliza seu mapeador
                .collect(Collectors.toList());
    }

    @Transactional
    public GeneratorConfigDTO getGeneratorById(Long generatorId) {
        GeneratorConfig generator = generatorConfigRepository.findById(generatorId)
                .orElseThrow(() -> new EntityNotFoundException("Gerador não encontrado com ID: " + generatorId));

        return toResponseDTO(generator);
    }

    /**
     * GET: Lista todos os observers de um gerador específico.
     */
    @Transactional
    public List<GenerateObserverDTO> getObserversByGenerator(Long generatorId) {
        GeneratorConfig generator = generatorConfigRepository.findById(generatorId)
                .orElseThrow(() -> new EntityNotFoundException("Gerador não encontrado: " + generatorId));

        return generator.getObservers().stream()
                .map(this::mapObserverToDTO)
                .collect(Collectors.toList());
    }

    /**
     * POST: Adiciona um novo observer padrão a um gerador existente.
     */
    @Transactional
    public GenerateObserverDTO addGeneratorObserver(Long generatorId, GenerateObserverRequestDTO request) {
        GeneratorConfig generator = generatorConfigRepository.findById(generatorId)
                .orElseThrow(() -> new EntityNotFoundException("Gerador não encontrado: " + generatorId));

        GeneratorObserver observer = new GeneratorObserver();

        int position = generator.getObservers().size() + 1;

        String queueName = generator.getTargetWorkProduct().getQueue_name();
        String defaultName = generator.getTargetWorkProduct().getQueue_name() + " generate activity observer " + position;

        observer.setGeneratorConfig(generator);
        observer.setPosition(position);
        observer.setQueue_name(queueName);
        observer.setName(defaultName);

        observer.setType(request.getType());

        GeneratorObserver savedObserver = generatorObserverRepository.save(observer);

        return mapObserverToDTO(savedObserver);
    }


    /**
     * PATCH: Atualiza campos específicos de um observer.
     */
    @Transactional
    public GenerateObserverDTO updateGeneratorObserver(Long observerId, GenerateObserverDTO dto) {
        GeneratorObserver observer = generatorObserverRepository.findById(observerId)
                .orElseThrow(() -> new EntityNotFoundException("Generator Observer não encontrado: " + observerId));

        if (dto.getName() != null) {
            observer.setName(dto.getName());
        }
        if (dto.getType() != null) {
            observer.setType(dto.getType());
        }
        if (dto.getQueueName() != null) {
            observer.setQueue_name(dto.getQueueName());
        }

        GeneratorObserver savedObserver = generatorObserverRepository.save(observer);
        return mapObserverToDTO(savedObserver);
    }

    /**
     * DELETE: Remove um observer específico.
     */
    @Transactional
    public void deleteGeneratorObserver(Long observerId) {
        GeneratorObserver observer = generatorObserverRepository.findById(observerId)
                .orElseThrow(() -> new EntityNotFoundException("Generate Observer não encontrado: " + observerId));

        generatorObserverRepository.delete(observer);
    }

    public List<WorkProductConfig> findAllByDeliveryProcessId(Long processId) {
        return workProductConfigRepository.findAllByDeliveryProcessIdOrderByIdAsc(processId);
    }

}