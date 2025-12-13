package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.ObserverUpdateDTO;
import com.example.projeto_tcc.dto.RoleConfigUpdateDTO;
import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.enums.MethodType;
import com.example.projeto_tcc.enums.ObserverMethodElementType;
import com.example.projeto_tcc.repository.MethodElementObserverRepository;
import com.example.projeto_tcc.repository.RoleConfigRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleConfigService {

    private final RoleConfigRepository configRepository;

    private final MethodElementObserverRepository observerRepository;


    @Transactional
    public void generateConfigurations(List<MethodElement> methodElements, DeliveryProcess deliveryProcess) {
        // Filtra apenas os MethodElements do tipo ROLE
        List<MethodElement> roleElements = methodElements.stream()
                .filter(me -> me.getMethodType() == MethodType.ROLE)
                .toList();

        // Agrupa por nome
        Map<String, List<MethodElement>> groupedByName = roleElements.stream()
                .collect(Collectors.groupingBy(MethodElement::getName));

        // Para cada grupo, cria um RoleConfig
        for (Map.Entry<String, List<MethodElement>> entry : groupedByName.entrySet()) {
            String roleName = entry.getKey();
            List<MethodElement> groupedRoles = entry.getValue();

            RoleConfig config = new RoleConfig();
            config.setName(roleName);
            config.setQueue_name(roleName + " queue");
            config.setQueue_type("QUEUE");
            config.setInitial_quantity(1);

            List<Long> ids = groupedRoles.stream()
                    .map(MethodElement::getId)
                    .collect(Collectors.toList());

            config.setRoleIds(ids);
            config.setDeliveryProcess(deliveryProcess);

            Set<Activity> relatedActivities = groupedRoles.stream()
                    .map(MethodElement::getParentActivity)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            config.setActivities(relatedActivities);

            // Cria e adiciona o Observer padrão
            MethodElementObserver observer = new MethodElementObserver();
            observer.setPosition(1);
            observer.setQueue_name(config.getQueue_name());
            observer.setName(config.getQueue_name() + " Observer " + observer.getPosition());
            observer.setType(ObserverMethodElementType.NONE);
            observer.setRoleConfig(config);
            config.getObservers().add(observer);

            configRepository.save(config);
        }
    }


    @Transactional
    public MethodElementObserver addObserverToRoleConfig(Long roleConfigId, ObserverMethodElementType type) {
        RoleConfig config = configRepository.findById(roleConfigId)
                .orElseThrow(() -> new IllegalArgumentException("RoleConfig not found"));

        int nextPosition = config.getObservers().stream()
                .mapToInt(Observer::getPosition)
                .max()
                .orElse(0) + 1;

        MethodElementObserver observer = new MethodElementObserver();
        observer.setPosition(nextPosition);
        observer.setQueue_name(config.getQueue_name());
        observer.setName(config.getQueue_name() + " Observer " + nextPosition);
        observer.setType(type != null ? type : ObserverMethodElementType.NONE);
        observer.setRoleConfig(config);

        config.getObservers().add(observer);

        return observerRepository.save(observer);
    }


    @Transactional
    public void removeObserverFromRoleConfig(Long roleConfigId, Long observerId) {
        RoleConfig config = configRepository.findById(roleConfigId)
                .orElseThrow(() -> new EntityNotFoundException("RoleConfig not found with ID: " + roleConfigId));

        MethodElementObserver observerToRemove = config.getObservers().stream()
                .filter(obs -> obs.getId().equals(observerId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Observer not found with ID: " + observerId));

        config.getObservers().remove(observerToRemove);
        observerRepository.delete(observerToRemove); // necessário para remover do banco
        configRepository.save(config); // persiste a mudança no RoleConfig (opcional, mas seguro)
    }

    @Transactional
    public MethodElementObserver updateObserver(Long observerId, ObserverUpdateDTO dto) {
        MethodElementObserver observer = observerRepository.findById(observerId)
                .orElseThrow(() -> new IllegalArgumentException("Observer not found with id: " + observerId));

        if (dto.getType() != null) {
            observer.setType(dto.getType());
        }
        if (dto.getQueueName() != null) {
            observer.setQueue_name(dto.getQueueName());
        }

        return observerRepository.save(observer);
    }


    @Transactional
    public List<RoleConfig> getRolesByDeliveryProcess(Long deliveryProcessId) {
        List<RoleConfig> roles = configRepository.findByDeliveryProcessId(deliveryProcessId);
        // Força carregar observers (para evitar LazyInitializationException)
        roles.forEach(role -> role.getObservers().size());
        return roles;
    }



    @Transactional
    public RoleConfig updateRoleConfig(Long roleConfigId, RoleConfigUpdateDTO dto) {
        RoleConfig config = configRepository.findById(roleConfigId)
                .orElseThrow(() -> new IllegalArgumentException("RoleConfig not found"));

        if (dto.getQueueName() != null) {
            config.setQueue_name(dto.getQueueName());
        }
        if (dto.getQueueType() != null) {
            config.setQueue_type(dto.getQueueType());
        }
        if (dto.getInitialQuantity() != null) {
            config.setInitial_quantity(dto.getInitialQuantity());
        }

        return configRepository.save(config);
    }







}
