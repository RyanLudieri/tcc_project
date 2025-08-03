package com.example.projeto_tcc.service;

import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.entity.Observer;
import com.example.projeto_tcc.enums.MethodType;
import com.example.projeto_tcc.enums.ObserverMethodElementType;
import com.example.projeto_tcc.repository.MethodElementObserverRepository;
import com.example.projeto_tcc.repository.ObserverRepository;
import com.example.projeto_tcc.repository.RoleConfigRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleConfigService {

    private final RoleConfigRepository configRepository;

    private final MethodElementObserverRepository observerRepository;


    @Transactional
    public void generateConfigurations(List<MethodElement> methodElements) {
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

            // Cria e adiciona o Observer padrão
            MethodElementObserver observer = new MethodElementObserver();
            observer.setPosition(1);
            observer.setQueue_name(config.getQueue_name());
            observer.setName(config.getQueue_name() + " Observer " + observer.getPosition());
            observer.setType(ObserverMethodElementType.LENGTH);
            observer.setRoleConfig(config);
            config.getObservers().add(observer);

            configRepository.save(config);
        }
    }


    @Transactional
    public MethodElementObserver addObserverToRoleConfig(Long roleConfigId) {
        RoleConfig config = configRepository.findById(roleConfigId)
                .orElseThrow(() -> new IllegalArgumentException("RoleConfig não encontrado"));

        int nextPosition = config.getObservers().stream()
                .mapToInt(Observer::getPosition)
                .max()
                .orElse(0) + 1;

        MethodElementObserver observer = new MethodElementObserver();
        observer.setPosition(nextPosition);
        observer.setQueue_name(config.getQueue_name());
        observer.setName(config.getQueue_name() + " Observer " + nextPosition);
        observer.setType(ObserverMethodElementType.LENGTH);
        observer.setRoleConfig(config);

        config.getObservers().add(observer);
        configRepository.save(config);

        return observer;
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



}
