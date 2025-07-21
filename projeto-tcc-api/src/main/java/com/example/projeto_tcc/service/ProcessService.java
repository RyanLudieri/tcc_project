package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.*;
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

    // Repositórios para acessar e persistir entidades do modelo
    private final ActivityRepository repository;
    private final MethodElementRepository methodElementRepository;

    // Construtor com injeção de dependência dos repositórios
    public ProcessService(ActivityRepository repository, MethodElementRepository methodElementRepository) {
        this.repository = repository;
        this.methodElementRepository = methodElementRepository;
    }

    // Índice utilizado para manter ordem e referenciar elementos
    private int currentIndex = 0;

    // Mapa auxiliar que armazena os elementos de processo indexados
    private Map<Integer, Activity> indexToActivity = new HashMap<>();

    /**
     * Salva um novo processo completo (DeliveryProcess) com seus elementos hierárquicos.
     * - Cria os elementos do processo (atividades) e resolve relações de predecessores e filhos.
     * - Associa elementos do método (MethodElements).
     * - Gera uma estrutura WBS e associa ao processo.
     *
     * @param dto Objeto com os dados do processo completo.
     * @return Entidade DeliveryProcess salva no banco de dados.
     */
    public Process saveProcess(ProcessDTO dto) {
        currentIndex = 0;
        indexToActivity.clear();

        // Criação da raiz do processo (DeliveryProcess)
        DeliveryProcess deliveryProcess = new DeliveryProcess();
        deliveryProcess.setName(dto.getName());
        deliveryProcess.setPredecessors(dto.getPredecessors());
        deliveryProcess.setType(ProcessType.DELIVERY_PROCESS);
        deliveryProcess.setIndex(currentIndex++);
        deliveryProcess.setOptional(dto.isOptional());

        // Criação da estrutura de decomposição (WBS)
        WorkBreakdownStructure wbs = new WorkBreakdownStructure();
        List<Activity> elements = new ArrayList<>();
        List<ProcessElementDTO> elementDTOs = dto.getProcessElements();

        // Etapa 1: cria os elementos do processo (sem setar predecessores ainda)
        for (ProcessElementDTO elemDto : elementDTOs) {
            Activity element = toEntityWithoutPredecessors(elemDto);
            elements.add(element);
        }

        // Etapa 2: resolve as relações de predecessores entre os elementos
        for (int i = 0; i < elementDTOs.size(); i++) {
            resolvePredecessors(elementDTOs.get(i), elements.get(i));
        }

        wbs.setProcessElements(elements);

        // Associa os elementos do método à estrutura
        List<MethodElement> methodElements = new ArrayList<>();
        if (dto.getMethodElements() != null) {
            for (MethodElementDTO methodDto : dto.getMethodElements()) {
                MethodElement method = toMethodEntity(methodDto);
                methodElements.add(method);
            }
        }
        wbs.setMethodElements(methodElements);

        // Associa a WBS ao processo
        deliveryProcess.setWbs(wbs);
        return repository.save(deliveryProcess);
    }

    /**
     * Constrói recursivamente os elementos do processo (Activity) a partir dos DTOs,
     * sem ainda tratar os predecessores. Também constrói a hierarquia de filhos.
     */
    private Activity toEntityWithoutPredecessors(ProcessElementDTO dto) {
        Activity entity = createProcessElementByType(dto.getType());
        entity.setName(dto.getName());
        entity.setOptional(dto.isOptional());

        indexToActivity.put(entity.getIndex(), entity);

        // Cria e associa os filhos recursivamente
        if (dto.getChildren() != null) {
            List<Activity> children = new ArrayList<>();
            for (ProcessElementDTO childDto : dto.getChildren()) {
                Activity child = toEntityWithoutPredecessors(childDto);
                child.setSuperActivity(entity); // Define o pai (superActivity)
                children.add(child);
            }
            entity.setChildren(children);
        }
        return entity;
    }

    /**
     * Após os elementos estarem criados, este método resolve e associa os predecessores corretamente.
     */
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

        // Resolve os predecessores também nos filhos recursivamente
        if (dto.getChildren() != null && entity.getChildren() != null) {
            for (int i = 0; i < dto.getChildren().size(); i++) {
                resolvePredecessors(dto.getChildren().get(i), entity.getChildren().get(i));
            }
        }
    }

    /**
     * Cria uma instância de Activity baseada no tipo (Task, Role, Phase, etc.)
     * e configura seu índice e tipo.
     */
    private Activity createProcessElementByType(ProcessType type) {
        Activity element = type.createInstance(); // Polimorfismo na criação
        element.setIndex(currentIndex++);
        element.setType(type);
        return element;
    }

    /**
     * Converte um DTO de MethodElement para a entidade correspondente.
     * Também associa à atividade pai, se o índice for fornecido.
     */
    private MethodElement toMethodEntity(MethodElementDTO dto) {
        MethodElement element = dto.getType().createInstance();
        element.setName(dto.getName());
        element.setModelInfo(dto.getModelInfo());
        element.setOptional(dto.isOptional());

        if (dto.getParentIndex() != null) {
            Activity parent = indexToActivity.get(dto.getParentIndex());
            if (parent != null) {
                element.setParentActivity(parent);
            }
        }

        return element;
    }

    /**
     * Atualiza campos genéricos de uma atividade (Activity), como nome.
     */
    @Transactional
    public Activity updateGenericActivity(Long id, ProcessElementDTO dto) {
        Activity activity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Elemento não encontrado com id: " + id));

        if (dto.getName() != null) activity.setName(dto.getName());
        activity.setOptional(dto.isOptional());

        return repository.save(activity);
    }

    /**
     * Atualiza campos de um elemento do método (MethodElement), como nome e modelInfo.
     */
    @Transactional
    public MethodElement updateGenericMethod(Long id, MethodElementDTO dto) {
        MethodElement element = methodElementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Elemento não encontrado com id: " + id));

        if (dto.getName() != null) element.setName(dto.getName());
        if (dto.getModelInfo() != null) element.setModelInfo(dto.getModelInfo());
        element.setOptional(dto.isOptional());

        return methodElementRepository.save(element);
    }

    /**
     * Deleta um elemento do processo ou método baseado no ID.
     * Verifica em ambos os repositórios.
     */
    public void deleteElementById(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else if (methodElementRepository.existsById(id)) {
            methodElementRepository.deleteById(id);
        } else {
            throw new RuntimeException("Elemento com ID " + id + " não encontrado.");
        }
    }

    /**
     * Converte uma Activity (ou DeliveryProcess) para o DTO de retorno (ProcessGetDTO),
     * incluindo seus elementos e estrutura interna, se houver.
     */
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
                // Process Elements (Activities)
                if (wbs.getProcessElements() != null) {
                    List<ProcessElementDTO> elementDTOs = wbs.getProcessElements().stream()
                            .map(this::convertToProcessElementDTO)
                            .toList();
                    dto.setProcessElements(elementDTOs);
                }

                // Method Elements (como Role, WorkProduct etc.)
                if (wbs.getMethodElements() != null) {
                    List<GetMethodElementDTO> methodDTOs = wbs.getMethodElements().stream()
                            .map(this::convertToGetMethodDTO)
                            .toList();
                    dto.setMethodElements(methodDTOs);
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
        // outros campos, se houver
        return dto;
    }




    /**
     * Converte recursivamente uma Activity para o DTO correspondente, incluindo filhos.
     */
    private ProcessElementDTO convertToProcessElementDTO(Activity entity) {
        ProcessElementDTO dto = new ProcessElementDTO();
        dto.setName(entity.getName());
        dto.setIndex(entity.getIndex());
        dto.setModelInfo(entity.getModelInfo());
        dto.setType(entity.getType());
        dto.setPredecessors(entity.getPredecessors() == null ? null :
                entity.getPredecessors().stream().map(Activity::getIndex).toList());

        // Converte filhos recursivamente
        if (entity.getChildren() != null) {
            List<ProcessElementDTO> childrenDto = entity.getChildren().stream()
                    .map(this::convertToProcessElementDTO)
                    .toList();
            dto.setChildren(childrenDto);
        }

        dto.setOptional(entity.isOptional());

        return dto;
    }
}



