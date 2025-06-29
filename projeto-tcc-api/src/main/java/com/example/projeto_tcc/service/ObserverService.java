package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.ActivitySummaryDTO;
import com.example.projeto_tcc.dto.ObserverDTO;
import com.example.projeto_tcc.entity.Activity;
import com.example.projeto_tcc.entity.Observer;
import com.example.projeto_tcc.entity.Role;
import com.example.projeto_tcc.entity.WorkProduct;
import com.example.projeto_tcc.repository.ActivityRepository;
import com.example.projeto_tcc.repository.ObserverRepository;
import com.example.projeto_tcc.repository.RoleRepository;
import com.example.projeto_tcc.repository.WorkProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ObserverService {

    private final ObserverRepository observerRepository;
    private final ActivityRepository activityRepository;
    private final RoleRepository roleRepository;

    private final WorkProductRepository workProductRepository;

    // Construtor com injeção dos repositórios
    public ObserverService(ObserverRepository observerRepository,
                           ActivityRepository activityRepository,
                           RoleRepository roleRepository,
                           WorkProductRepository workProductRepository) {
        this.observerRepository = observerRepository;
        this.activityRepository = activityRepository;
        this.roleRepository = roleRepository;
        this.workProductRepository = workProductRepository;
    }

    /**
     * Cria e persiste um novo Observer.
     * - Se informado, associa o Observer à Activity existente.
     * - Se informado, associa o Observer ao Role existente.
     * - Retorna o DTO do Observer criado, incluindo resumo da Activity associada.
     *
     * @param observer Entidade Observer com dados para criação.
     * @return DTO representando o Observer criado.
     */
    public ObserverDTO createObserver(Observer observer) {
        // Associa a Activity, se ID estiver presente
        if (observer.getActivity() != null && observer.getActivity().getId() != null) {
            Long activityId = observer.getActivity().getId();
            Activity activity = activityRepository.findById(activityId)
                    .orElseThrow(() -> new IllegalArgumentException("Activity not found with id: " + activityId));
            observer.setActivity(activity);
        }

        // Associa o Role, se ID estiver presente
        if (observer.getRole() != null && observer.getRole().getId() != null) {
            Long roleId = observer.getRole().getId();
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));
            observer.setRole(role);
        }

        // Associa o WorkProduct, se ID estiver presente
        if (observer.getWorkproduct() != null && observer.getWorkproduct().getId() != null){
            Long workProductId = observer.getWorkproduct().getId();
            WorkProduct workProduct = workProductRepository.findById(workProductId)
                    .orElseThrow(() -> new IllegalArgumentException("Work Product not found with id: " + workProductId));
            observer.setWorkproduct(workProduct);
        }

        // Persiste o Observer no banco
        Observer saved = observerRepository.save(observer);

        // Cria DTO de resumo da Activity associada (se houver)
        ActivitySummaryDTO activityDTO = null;
        if (saved.getActivity() != null) {
            activityDTO = new ActivitySummaryDTO(
                    saved.getActivity().getId(),
                    saved.getActivity().getName(),
                    saved.getActivity().getType()
            );
        }

        // Retorna o DTO completo do Observer
        return new ObserverDTO(
                saved.getId(),
                saved.getName(),
                saved.getType(),
                activityDTO,
                saved.getRole() != null ? saved.getRole().getId() : null,
                saved.getWorkproduct() != null ? saved.getWorkproduct().getId() : null
        );
    }
}

