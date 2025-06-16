package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.ActivityResponseDTO;
import com.example.projeto_tcc.dto.RoleMappingDTO;
import com.example.projeto_tcc.dto.RoleResponseDTO;
import com.example.projeto_tcc.dto.SimulationParamsDTO;
import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.repository.*;
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

    private final RoleRepository roleRepository;

    public SimulationService(ActivityRepository activityRepository,
                             SampleRepository sampleRepository,
                             ObserverRepository observerRepository,
                             DurationMeasurementRepository durationMeasurementRepository,
                             RoleRepository roleRepository) {
        this.activityRepository = activityRepository;
        this.sampleRepository = sampleRepository;
        this.observerRepository = observerRepository;
        this.durationMeasurementRepository = durationMeasurementRepository;
        this.roleRepository = roleRepository;
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

        activity.configureFromDTO(dto);

        return activityRepository.save(activity);
    }


    public ActivityResponseDTO toActivityResponseDTO(Activity activity) {
        // Aqui o polimorfismo funciona: chama o método correto em Activity ou suas subclasses
        return activity.toSimulationDTO();
    }

    @Transactional
    public RoleResponseDTO mapRoleFields(RoleMappingDTO dto) {
        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + dto.getRoleId()));

        if (dto.getQueueName() != null) role.setQueue_name(dto.getQueueName());
        if (dto.getQueueType() != null) role.setQueue_type(dto.getQueueType());
        if (dto.getInitialQuantity() != null) role.setInitial_quantity(dto.getInitialQuantity());

        if (dto.getObserverIds() != null) {
            List<Observer> observers = observerRepository.findAllById(dto.getObserverIds());
            role.setObservers(observers);
            for (Observer obs : observers) {
                obs.setRole(role); // se bidirecional, mantenha consistência
            }
        }

        Role updatedRole = roleRepository.save(role);
        return toRoleResponseDTO(updatedRole);
    }


    public RoleResponseDTO toRoleResponseDTO(Role role) {
        return new RoleResponseDTO(
                role.getId(),
                role.getQueue_name(),
                role.getQueue_type(),
                role.getInitial_quantity()
        );
    }



}

