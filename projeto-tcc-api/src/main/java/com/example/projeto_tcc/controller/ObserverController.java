package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.ActivitySummaryDTO;
import com.example.projeto_tcc.dto.ObserverDTO;
import com.example.projeto_tcc.entity.Activity;
import com.example.projeto_tcc.entity.Observer;
import com.example.projeto_tcc.repository.ActivityRepository;
import com.example.projeto_tcc.repository.ObserverRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/observers")
public class ObserverController {
    private final ObserverRepository observerRepository;

    private final ActivityRepository activityRepository;

    public ObserverController(ObserverRepository observerRepository, ActivityRepository activityRepository) {
        this.observerRepository = observerRepository;
        this.activityRepository = activityRepository;
    }

    @PostMapping
    public ResponseEntity<ObserverDTO> createObserver(@RequestBody Observer observer) {
        // Carrega a Activity completa pelo ID, se existir
        if (observer.getActivity() != null && observer.getActivity().getId() != null) {
            Long activityId = observer.getActivity().getId();
            Activity activityEntity = activityRepository.findById(activityId)
                    .orElseThrow(() -> new IllegalArgumentException("Activity not found with id: " + activityId));
            observer.setActivity(activityEntity);
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

        ObserverDTO dto = new ObserverDTO(saved.getId(), saved.getName(), saved.getType(), activityDTO);
        return ResponseEntity.ok(dto);
    }


}
