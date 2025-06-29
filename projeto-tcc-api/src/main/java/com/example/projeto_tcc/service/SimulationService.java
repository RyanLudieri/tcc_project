package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.*;
import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.repository.*;
import com.example.projeto_tcc.util.DistributionFactory;
import com.example.projeto_tcc.util.MeasurementFactory;
import jakarta.transaction.Transactional;
import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SimulationService {

    // Repositórios necessários para operações de banco de dados
    private final ActivityRepository activityRepository;
    private final SampleRepository sampleRepository;
    private final ObserverRepository observerRepository;
    private final DurationMeasurementRepository durationMeasurementRepository;
    private final RoleRepository roleRepository;

    private final WorkProductRepository workProductRepository;

    // Construtor com injeção de dependências via Spring
    public SimulationService(ActivityRepository activityRepository,
                             SampleRepository sampleRepository,
                             ObserverRepository observerRepository,
                             DurationMeasurementRepository durationMeasurementRepository,
                             RoleRepository roleRepository, WorkProductRepository workProductRepository) {
        this.activityRepository = activityRepository;
        this.sampleRepository = sampleRepository;
        this.observerRepository = observerRepository;
        this.durationMeasurementRepository = durationMeasurementRepository;
        this.roleRepository = roleRepository;
        this.workProductRepository = workProductRepository;
    }

    /**
     * Configura os parâmetros de simulação para uma atividade específica.
     * - Associa uma amostra (Sample) à atividade.
     * - Gera medições de duração com base na distribuição estatística.
     * - Associa observadores à atividade.
     * - Aplica configurações adicionais da simulação usando `configureFromDTO`.
     *
     * @param dto Dados de entrada com os parâmetros da simulação.
     * @return Activity atualizada e salva no banco.
     */
    @Transactional
    public Activity setSimulationParameters(SimulationParamsDTO dto) {
        // Busca a atividade pelo ID
        Activity activity = activityRepository.findById(dto.getActivityId())
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        // Se foi informada uma amostra, associa à atividade e gera medições
        if (dto.getSampleId() != null) {
            Sample sample = sampleRepository.findById(dto.getSampleId())
                    .orElseThrow(() -> new RuntimeException("Sample not found"));
            activity.setSample(sample);

            // Cria a distribuição de duração a partir dos parâmetros da amostra
            Object distribution = DistributionFactory.createDistribution(
                    sample.getDistribution(), sample.getParameter());

            // Gera medições com base no tipo de distribuição
            List<DurationMeasurement> measurements;
            if (distribution instanceof RealDistribution realDist) {
                measurements = MeasurementFactory.createRealDistributionForDuration(realDist, sample.getSize(), activity.getTimeScale());
            } else if (distribution instanceof IntegerDistribution intDist) {
                measurements = MeasurementFactory.createIntegerDistributionForDuration(intDist, sample.getSize(), activity.getTimeScale());
            } else {
                throw new RuntimeException("Unsupported distribution type.");
            }

            // Relaciona medições com a atividade e a amostra
            for (DurationMeasurement m : measurements) {
                m.setSample(sample);
                m.setActivity(activity);
            }

            // Salva todas as medições no banco
            durationMeasurementRepository.saveAll(measurements);
            sample.setMeasurements(measurements); // Atualiza o sample com suas medições
        }

        // Se foram informados observadores, associa-os à atividade
        if (dto.getObserverIds() != null) {
            List<Observer> observers = observerRepository.findAllById(dto.getObserverIds());
            for (Observer observer : observers) {
                observer.setActivity(activity); // associação bidirecional
            }
            activity.setObservers(observers);
        }

        // Aplica configurações adicionais específicas da atividade
        activity.configureFromDTO(dto);

        // Salva e retorna a atividade configurada
        return activityRepository.save(activity);
    }

    /**
     * Converte uma Activity para seu DTO de resposta.
     * Usa polimorfismo: se for uma subclasse (ex: Task, Process), chama a versão correta.
     *
     * @param activity A entidade Activity.
     * @return DTO com informações de simulação.
     */
    public ActivityResponseDTO toActivityResponseDTO(Activity activity) {
        return activity.toSimulationDTO();
    }

    /**
     * Atualiza os campos de uma Role com base nos dados do DTO.
     * Pode alterar: nome e tipo da fila, quantidade inicial e observadores associados.
     *
     * @param dto DTO contendo os dados para atualização.
     * @return DTO com os dados atualizados da Role.
     */
    @Transactional
    public RoleResponseDTO mapRoleFields(RoleMappingDTO dto) {
        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + dto.getRoleId()));

        // Atualiza atributos básicos se presentes
        if (dto.getQueueName() != null) role.setQueue_name(dto.getQueueName());
        if (dto.getQueueType() != null) role.setQueue_type(dto.getQueueType());
        if (dto.getInitialQuantity() != null) role.setInitial_quantity(dto.getInitialQuantity());

        // Atualiza observadores se presentes
        if (dto.getObserverIds() != null) {
            List<Observer> observers = observerRepository.findAllById(dto.getObserverIds());
            role.setObservers(observers);
            for (Observer obs : observers) {
                obs.setRole(role); // associação bidirecional
            }
        }

        // Salva e retorna a Role atualizada
        Role updatedRole = roleRepository.save(role);
        return toRoleResponseDTO(updatedRole);
    }



    /**
     * Converte uma entidade Role para seu DTO de resposta.
     *
     * @param role A entidade Role.
     * @return DTO com os dados essenciais da Role.
     */
    public RoleResponseDTO toRoleResponseDTO(Role role) {
        return new RoleResponseDTO(
                role.getId(),
                role.getQueue_name(),
                role.getQueue_type(),
                role.getInitial_quantity()
        );
    }

    @Transactional
    public WorkProductResponseDTO mapWorkProductFields(WorkProductDTO dto) {
        WorkProduct workProduct = workProductRepository.findById(dto.getWorkProductId())
                .orElseThrow(() -> new RuntimeException("WorkProduct not found with id: " + dto.getWorkProductId()));

        if (dto.getTaskName() != null) workProduct.setTask_name(dto.getTaskName());
        if (dto.getQueueName() != null) workProduct.setQueue_name(dto.getQueueName());
        if (dto.getQueueType() != null) workProduct.setQueue_type(dto.getQueueType());
        if (dto.getQueueSize() != null) workProduct.setQueue_size(dto.getQueueSize());
        if (dto.getInitialQuantity() != null) workProduct.setInitial_quantity(dto.getInitialQuantity());
        if (dto.getPolicy() != null) workProduct.setPolicy(dto.getPolicy());

        // Mapeia e associa os Observers
        if (dto.getObserverIds() != null) {
            List<Observer> observers = observerRepository.findAllById(dto.getObserverIds());
            workProduct.setObservers(observers);
            for (Observer observer : observers) {
                observer.setWorkproduct(workProduct); // ou setParent(workProduct), dependendo do seu modelo
            }
        }

        WorkProduct updated = workProductRepository.save(workProduct);
        return toWorkProductResponseDTO(updated);
    }

    public WorkProductResponseDTO toWorkProductResponseDTO(WorkProduct wp) {
        return new WorkProductResponseDTO(
                wp.getId(),
                wp.getName(),
                wp.getModelInfo(),
                wp.getType(),
                wp.getTask_name(),
                wp.getQueue_name(),
                wp.getQueue_type(),
                wp.getQueue_size(),
                wp.getInitial_quantity(),
                wp.getPolicy()
        );
    }


}


