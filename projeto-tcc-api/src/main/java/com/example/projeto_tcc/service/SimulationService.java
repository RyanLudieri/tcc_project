package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.ActivityResponseDTO;
import com.example.projeto_tcc.dto.SimulationParamsDTO;
import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.repository.ActivityRepository;
import com.example.projeto_tcc.repository.DurationMeasurementRepository;
import com.example.projeto_tcc.repository.ObserverRepository;
import com.example.projeto_tcc.repository.SampleRepository;
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

    private final ActivityRepository activityRepository;
    private final SampleRepository sampleRepository;
    private final ObserverRepository observerRepository;

    private final DurationMeasurementRepository durationMeasurementRepository;

    public SimulationService(ActivityRepository activityRepository,
                             SampleRepository sampleRepository,
                             ObserverRepository observerRepository,
                             DurationMeasurementRepository durationMeasurementRepository) {
        this.activityRepository = activityRepository;
        this.sampleRepository = sampleRepository;
        this.observerRepository = observerRepository;
        this.durationMeasurementRepository = durationMeasurementRepository;
    }

    @Transactional
    public Activity setSimulationParameters(SimulationParamsDTO dto) {
        Activity activity = activityRepository.findById(dto.getActivityId())
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        // Associa Sample e gera medições (se aplicável)
        if (dto.getSampleId() != null) {
            Sample sample = sampleRepository.findById(dto.getSampleId())
                    .orElseThrow(() -> new RuntimeException("Sample not found"));
            activity.setSample(sample);

            Object distribution = DistributionFactory.createDistribution(
                    sample.getDistribution(), sample.getParameter());

            List<DurationMeasurement> measurements;
            if (distribution instanceof RealDistribution realDist) {
                measurements = MeasurementFactory.createRealDistributionForDuration(realDist, sample.getSize(), activity.getTimeScale());
            } else if (distribution instanceof IntegerDistribution intDist) {
                measurements = MeasurementFactory.createIntegerDistributionForDuration(intDist, sample.getSize(), activity.getTimeScale());
            } else {
                throw new RuntimeException("Unsupported distribution type.");
            }

            for (DurationMeasurement m : measurements) {
                m.setSample(sample);
                m.setActivity(activity);
            }

            durationMeasurementRepository.saveAll(measurements);
            sample.setMeasurements(measurements);
        }

        // Associa Observers (se aplicável)
        if (dto.getObserverIds() != null) {
            List<Observer> observers = observerRepository.findAllById(dto.getObserverIds());
            for (Observer observer : observers) {
                observer.setActivity(activity);
            }
            activity.setObservers(observers);
        }

        // Configurações específicas por tipo
        switch (activity.getType()) {
            case ITERATION -> {
                Iteration iteration = (Iteration) activity;
                iteration.setTimeBox(dto.getTimeBox());
                iteration.setTimeScale(dto.getTimeScale());
                iteration.setConditionToProcess(dto.getConditionToProcess());
                iteration.setProcessingQuantity(dto.getProcessingQuantity());
                iteration.setIterationBehavior(dto.getIterationBehavior());
                // Sample e Observer aplicáveis
            }

            case TASK_DESCRIPTOR -> {
                TaskDescriptor task = (TaskDescriptor) activity;
                task.setConditionToProcess(dto.getConditionToProcess());
                task.setProcessingQuantity(dto.getProcessingQuantity());
                task.setRequiredResources(dto.getRequiredResources());
                // Sample e Observer aplicáveis
            }

            case ACTIVITY -> {
                activity.setTimeBox(dto.getTimeBox());
                activity.setTimeScale(dto.getTimeScale());
                activity.setConditionToProcess(dto.getConditionToProcess());
                activity.setProcessingQuantity(dto.getProcessingQuantity());
                // Sample e Observer aplicáveis
            }

            case MILESTONE -> {
                Milestone milestone = (Milestone) activity;
                milestone.setDependencyType(dto.getDependencyType());
                milestone.setConditionToProcess(dto.getConditionToProcess());
                milestone.setProcessingQuantity(dto.getProcessingQuantity());
                // NÃO usa sample nem observer
                milestone.setSample(null);
                milestone.setObservers(null);
            }

            case PHASE -> {
                Phase phase = (Phase) activity;
                phase.setTimeBox(dto.getTimeBox());
                phase.setTimeScale(dto.getTimeScale());
                phase.setConditionToProcess(dto.getConditionToProcess());
                phase.setProcessingQuantity(dto.getProcessingQuantity());
                // NÃO usa sample nem observer
                phase.setSample(null);
                phase.setObservers(null);
            }

            default -> throw new IllegalArgumentException("Tipo de activity não reconhecido: " + activity.getType());
        }

        return activityRepository.save(activity);
    }


    public ActivityResponseDTO toActivityResponseDTO(Activity activity) {
        List<Long> observerIds = activity.getObservers() == null
                ? List.of()
                : activity.getObservers().stream()
                .map(Observer::getId)  // supondo que getId() retorna Long
                .collect(Collectors.toList());


        Integer sampleId = activity.getSample() != null ? activity.getSample().getId() : null;

        return new ActivityResponseDTO(
                activity.getId(),
                activity.getName(),
                activity.getType(),
                activity.getRequiredResources(),
                activity.getTimeBox(),
                activity.getTimeScale(),
                activity.getDependencyType(),
                activity.getConditionToProcess(),
                activity.getProcessingQuantity(),
                activity.getIterationBehavior(),
                observerIds,
                sampleId
        );
    }

}

