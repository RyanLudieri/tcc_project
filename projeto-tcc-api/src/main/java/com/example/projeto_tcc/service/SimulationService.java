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

    public Activity setSimulationParameters(SimulationParamsDTO dto) {
        Activity activity = activityRepository.findById(dto.getActivityId())
                .orElseThrow(() -> new RuntimeException("Activity not found"));

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

        if (dto.getObserverIds() != null) {
            List<Observer> observers = observerRepository.findAllById(dto.getObserverIds());
            for (Observer observer : observers) {
                observer.setActivity(activity);
            }
            activity.setObservers(observers);
        }

        activity.setDependencyType(dto.getDependencyType());
        activity.setConditionToProcess(dto.getConditionToProcess());
        activity.setProcessingQuantity(dto.getProcessingQuantity());
        activity.setIterationBehavior(dto.getIterationBehavior());
        activity.setRequiredResources(dto.getRequiredResources());
        if (dto.getTimeScale() == null) {
            throw new RuntimeException("TimeScale está null no DTO");
        }
        activity.setTimeScale(dto.getTimeScale());

        activityRepository.save(activity);

        return activity;
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

