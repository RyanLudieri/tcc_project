package com.example.projeto_tcc.service;

import com.example.projeto_tcc.entity.Activity;
import com.example.projeto_tcc.entity.MethodElement;
import com.example.projeto_tcc.entity.RoleConfig;
import com.example.projeto_tcc.enums.MethodType;
import com.example.projeto_tcc.repository.RoleConfigRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleConfigService {

    private final RoleConfigRepository configRepository;

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
            config.setInitial_quantity(1); // valor padrão

            List<Long> ids = groupedRoles.stream()
                    .map(MethodElement::getId)
                    .collect(Collectors.toList());

            config.setRoleIds(ids);

            configRepository.save(config);
        }
    }
}
