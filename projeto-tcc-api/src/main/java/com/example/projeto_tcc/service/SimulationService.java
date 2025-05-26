package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.SimulationParamsDTO;
import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.repository.ActivityRepository;
import com.example.projeto_tcc.repository.ObserverRepository;
import com.example.projeto_tcc.repository.SampleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimulationService {

    private final ActivityRepository activityRepository;
    private final SampleRepository sampleRepository;
    private final ObserverRepository observerRepository;

    public SimulationService(ActivityRepository activityRepository,
                             SampleRepository sampleRepository,
                             ObserverRepository observerRepository) {
        this.activityRepository = activityRepository;
        this.sampleRepository = sampleRepository;
        this.observerRepository = observerRepository;
    }

    public void setSimulationParameters(SimulationParamsDTO dto) {
        Activity activity = activityRepository.findById(dto.getActivityId())
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        if (dto.getSampleId() != null) {
            Sample sample = sampleRepository.findById(dto.getSampleId())
                    .orElseThrow(() -> new RuntimeException("Sample not found"));
            activity.setSample(sample);
        }

        if (dto.getObserverIds() != null) {
            List<Observer> observers = observerRepository.findAllById(dto.getObserverIds());
            for (Observer observer : observers) {
                observer.setActivity(activity); // ou setActivity(), dependendo do nome do campo
            }
            activity.setObservers(observers);
        }

        activity.setDependencyType(dto.getDependencyType());
        activity.setConditionToProcess(dto.getConditionToProcess());
        activity.setProcessingQuantity(dto.getProcessingQuantity());
        activity.setIterationBehavior(dto.getIterationBehavior());
        activity.setRequiredResources(dto.getRequiredResources());

        activityRepository.save(activity);
    }
}

