package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.ActivitySummaryDTO;
import com.example.projeto_tcc.dto.ObserverDTO;
import com.example.projeto_tcc.entity.Activity;
import com.example.projeto_tcc.entity.Observer;
import com.example.projeto_tcc.entity.Role;
import com.example.projeto_tcc.repository.ActivityRepository;
import com.example.projeto_tcc.repository.ObserverRepository;
import com.example.projeto_tcc.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class ObserverService {

    private final ObserverRepository observerRepository;
    private final ActivityRepository activityRepository;
    private final RoleRepository roleRepository;

    public ObserverService(ObserverRepository observerRepository,
                           ActivityRepository activityRepository,
                           RoleRepository roleRepository) {
        this.observerRepository = observerRepository;
        this.activityRepository = activityRepository;
        this.roleRepository = roleRepository;
    }

    public ObserverDTO createObserver(Observer observer) {
        // Associa a Activity, se informada
        if (observer.getActivity() != null && observer.getActivity().getId() != null) {
            Long activityId = observer.getActivity().getId();
            Activity activity = activityRepository.findById(activityId)
                    .orElseThrow(() -> new IllegalArgumentException("Activity not found with id: " + activityId));
            observer.setActivity(activity);
        }

        // Associa o Role, se informado
        if (observer.getRole() != null && observer.getRole().getId() != null) {
            Long roleId = observer.getRole().getId();
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));
            observer.setRole(role);
        }

        Observer saved = observerRepository.save(observer);

        ActivitySummaryDTO activityDTO = null;
        if (saved.getActivity() != null) {
            activityDTO = new ActivitySummaryDTO(
                    saved.getActivity().getId(),
                    saved.getActivity().getName(),
                    saved.getActivity().getType()
            );
        }

        return new ObserverDTO(saved.getId(), saved.getName(), saved.getType(), activityDTO);
    }
}

