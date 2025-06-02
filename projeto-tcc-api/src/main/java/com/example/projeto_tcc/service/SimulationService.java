package com.example.projeto_tcc.service;

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

    public void setSimulationParameters(SimulationParamsDTO dto) {
        Activity activity = activityRepository.findById(dto.getActivityId())
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        if (dto.getSampleId() != null) {
            Sample sample = sampleRepository.findById(dto.getSampleId())
                    .orElseThrow(() -> new RuntimeException("Sample not found"));
            activity.setSample(sample);

            // Gerar distribuições e medições a partir da amostra
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
            sample.setMeasurements(measurements); // opcional
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

        activityRepository.save(activity);
    }

}

