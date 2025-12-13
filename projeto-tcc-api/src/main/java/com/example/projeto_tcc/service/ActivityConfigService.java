package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.ActivityConfigDTO;
import com.example.projeto_tcc.dto.ActivityObserverDTO;
import com.example.projeto_tcc.dto.DistributionParameterDTO;
import com.example.projeto_tcc.dto.ObserverActivityDTO;
import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.enums.*;
import com.example.projeto_tcc.repository.*;
import com.example.projeto_tcc.util.MeasurementFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityConfigService {

    private final ActivityConfigRepository configRepository;
    private final DistributionParameterRepository parameterRepository;
    private final DurationMeasurementRepository measurementRepository;
    private final ObserverRepository observerRepository;
    private final ActivityObserverRepository activityObserverRepository;
    private final ActivityRepository activityRepository;

    @Transactional
    public ActivityConfig createDefaultConfig(Activity activity) {
        ActivityConfig config = new ActivityConfig();
        config.setActivity(activity);
        activity.setActivityConfig(config);

        setDefaultAttributesByActivityType(activity, config);

        int position = 1;
        if (config.getObservers() != null && !config.getObservers().isEmpty()) {
            position = config.getObservers().size() + 1;
        }

        ActivityObserver observer = new ActivityObserver();
        observer.setPosition(position);
        observer.setQueue_name(activity.getName());
        observer.setName(activity.getName() + " Observer " + position);
        observer.setType(ObserverActivityType.ACTIVE);
        observer.setActivityConfig(config);
        config.getObservers().add(observer);

        boolean isTask = activity instanceof TaskDescriptor;
        double defaultValue = isTask ? 480.0 : 0.0;
        int sampleSize = isTask ? 30 : 0;


        config.setDistributionType(BestFitDistribution.CONST);
        DistributionParameter param = new DistributionParameter();
        param.setConstant(defaultValue);
        //parameterRepository.save(param);
        config.setDistributionParameter(param);

        Sample sample = new Sample();
        sample.setName("Sample for " + activity.getName());
        sample.setDistribution(BestFitDistribution.CONST);
        sample.setParameter(param);
        sample.setSize(sampleSize);
        //sampleRepository.save(sample);
        config.setSample(sample);

        List<DurationMeasurement> measurements = new ArrayList<>();
        if (sampleSize > 0) {
            measurements = MeasurementFactory
                    .createConstantDurationMeasurements(defaultValue, sampleSize, activity.getTimeScale());
            for (DurationMeasurement m : measurements) {
                m.setActivity(activity);
                m.setSample(sample);
            }
            //measurementRepository.saveAll(measurements);
        }
        sample.setMeasurements(measurements);

        //configRepository.save(config);
        //activityRepository.save(activity);

        return config;
    }

    @Transactional
    public void createDefaultConfigsRecursively(Activity activity, DeliveryProcess deliveryProcess) {
        ActivityConfig config = createDefaultConfig(activity);
        config.setDeliveryProcess(deliveryProcess);

        //configRepository.save(config);

        if (activity.getChildren() != null) {
            for (Activity child : activity.getChildren()) {
                createDefaultConfigsRecursively(child, deliveryProcess);
            }
        }
    }


    private void setDefaultAttributesByActivityType(Activity activity, ActivityConfig config) {
        if (activity instanceof Phase) {
            config.setConditionToProcess(ConditionToProcess.SINGLE_ENTITY_AVAILABLE);
            config.setProcessingQuantity(ProcessingQuantity.UNIT);
        }
        else if (activity instanceof Iteration) {
            config.setDependencyType(DependencyType.FINISH_TO_START);
            config.setConditionToProcess(ConditionToProcess.SINGLE_ENTITY_AVAILABLE);
            config.setIterationBehavior(IterationBehavior.MOVE_BACK);
            config.setProcessingQuantity(ProcessingQuantity.UNIT);
            config.setTimeBox(0);
        }
        else if (activity instanceof TaskDescriptor) {
            config.setDependencyType(DependencyType.FINISH_TO_START);
            config.setConditionToProcess(ConditionToProcess.SINGLE_ENTITY_AVAILABLE);
            config.setTimeBox(0);
            config.setProcessingQuantity(ProcessingQuantity.UNIT);
            config.setRequiredResources(1);
        }
        else if (activity instanceof Milestone){
            config.setDependencyType(DependencyType.FINISH_TO_START);
            config.setConditionToProcess(ConditionToProcess.SINGLE_ENTITY_AVAILABLE);
            config.setProcessingQuantity(ProcessingQuantity.UNIT);
        }
        else {
            config.setDependencyType(DependencyType.FINISH_TO_START);
            config.setConditionToProcess(ConditionToProcess.SINGLE_ENTITY_AVAILABLE);
            config.setProcessingQuantity(ProcessingQuantity.UNIT);
            config.setTimeBox(0);
        }
    }


    @Transactional
    public ActivityConfigDTO getActivityConfig(Long activityId) {
        ActivityConfig config = configRepository.findByActivityId(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));

        DistributionParameter param = config.getDistributionParameter();
        DistributionParameterDTO paramDTO = null;
        if (param != null) {
            paramDTO = new DistributionParameterDTO();
            paramDTO.setId(param.getId());
            paramDTO.setAverage(param.getAverage());
            paramDTO.setMean(param.getMean());
            paramDTO.setConstant(param.getConstant());
            paramDTO.setStandardDeviation(param.getStandardDeviation());
            paramDTO.setLow(param.getLow());
            paramDTO.setHigh(param.getHigh());
            paramDTO.setShape(param.getShape());
            paramDTO.setScale(param.getScale());
        }

        List<ActivityObserverDTO> observers = config.getObservers().stream()
                .map(obs -> new ActivityObserverDTO(
                        obs.getId(),
                        obs.getName(),
                        obs.getQueue_name(),
                        obs.getPosition(),
                        obs.getType()
                ))
                .toList();

        return new ActivityConfigDTO(
                config.getDeliveryProcess() != null ? config.getDeliveryProcess().getId() : null,
                config.getDeliveryProcess() != null ? config.getDeliveryProcess().getName() : null,

                config.getActivity().getId(),
                config.getId(),
                config.getActivity().getName(),
                config.getActivity().getType().name(),
                config.getActivity().getSuperActivity() != null ? config.getActivity().getSuperActivity().getId() : null,

                config.getDependencyType(),
                config.getTimeBox(),
                config.getConditionToProcess(),
                config.getProcessingQuantity(),
                config.getIterationBehavior(),
                config.getRequiredResources(),
                config.getDistributionType(),
                paramDTO,
                observers
        );

    }

    @Transactional
    public ActivityConfigDTO updateActivityConfig(Long activityId, ActivityConfigDTO dto) {
        ActivityConfig config = configRepository.findByActivityId(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));

        if (dto.getDependencyType() != null) config.setDependencyType(dto.getDependencyType());
        if (dto.getTimeBox() >= 0) config.setTimeBox(dto.getTimeBox());
        if (dto.getConditionToProcess() != null) config.setConditionToProcess(dto.getConditionToProcess());
        if (dto.getProcessingQuantity() != null) config.setProcessingQuantity(dto.getProcessingQuantity());
        if (dto.getIterationBehavior() != null) config.setIterationBehavior(dto.getIterationBehavior());
        if (dto.getRequiredResources() > 0) config.setRequiredResources(dto.getRequiredResources());

        if (dto.getDistributionType() != null) config.setDistributionType(dto.getDistributionType());

        DistributionParameterDTO paramDTO = dto.getDistributionParameter();
        DistributionParameter param = config.getDistributionParameter();
        if (paramDTO != null && param != null) {
            param.setConstant(paramDTO.getConstant());
            param.setStandardDeviation(paramDTO.getStandardDeviation());
            param.setLow(paramDTO.getLow());
            param.setAverage(paramDTO.getAverage());
            param.setMean(paramDTO.getMean());
            param.setHigh(paramDTO.getHigh());
            param.setShape(paramDTO.getShape());
            param.setScale(paramDTO.getScale());
        }

        configRepository.save(config);

        return getActivityConfig(activityId);
    }

    @Transactional
    public List<ObserverActivityDTO> getObserversByActivityConfig(Long activityConfigId) {
        ActivityConfig config = configRepository.findById(activityConfigId)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));

        return config.getObservers().stream()
                .map(obs -> new ObserverActivityDTO(
                        obs.getId(),
                        obs.getQueue_name(),
                        obs.getName(),
                        obs.getPosition(),
                        obs.getType(),
                        obs.getActivityConfig().getId()
                ))
                .toList();
    }

    @Transactional
    public ObserverActivityDTO addObserver(Long activityConfigId) {
        ActivityConfig config = configRepository.findById(activityConfigId)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found"));

        int position = config.getObservers().size() + 1;

        ActivityObserver observer = new ActivityObserver();
        observer.setActivityConfig(config);
        observer.setPosition(position);
        observer.setQueue_name(config.getActivity().getName());
        observer.setName(config.getActivity().getName() + " Observer " + position);
        observer.setType(ObserverActivityType.ACTIVE);

        observerRepository.save(observer);
        config.getObservers().add(observer);

        return new ObserverActivityDTO(
                observer.getId(),
                observer.getQueue_name(),
                observer.getName(),
                observer.getPosition(),
                observer.getType(),
                config.getId()
        );
    }


    @Transactional
    public ObserverActivityDTO updateObserver(Long observerId, ObserverActivityDTO dto) {
        ActivityObserver observer = activityObserverRepository.findById(observerId)
                .orElseThrow(() -> new IllegalArgumentException("Observer not found"));

        if (dto.queueName() != null) observer.setQueue_name(dto.queueName());
        if (dto.name() != null) observer.setName(dto.name());
        if (dto.position() != null) observer.setPosition(dto.position());
        if (dto.type() != null) observer.setType(dto.type());

        observerRepository.save(observer);

        return new ObserverActivityDTO(
                observer.getId(),
                observer.getQueue_name(),
                observer.getName(),
                observer.getPosition(),
                observer.getType(),
                observer.getActivityConfig().getId()
        );
    }

    @Transactional
    public void removeObserver(Long observerId) {
        ActivityObserver observer = activityObserverRepository.findById(observerId)
                .orElseThrow(() -> new IllegalArgumentException("Observer not found"));

        observerRepository.delete(observer);
    }


    @Transactional
    public List<ActivityConfigDTO> getActivityByDeliveryProcess(Long deliveryProcessId) {
        List<ActivityConfig> configs = configRepository.findByDeliveryProcessId(deliveryProcessId);

        configs.forEach(c -> c.getObservers().size());

        List<ActivityConfigDTO> dtos = configs.stream()
                .map(config -> {
                    DistributionParameter param = config.getDistributionParameter();
                    DistributionParameterDTO paramDTO = null;
                    if (param != null) {
                        paramDTO = new DistributionParameterDTO();
                        paramDTO.setId(param.getId());
                        paramDTO.setConstant(param.getConstant());
                        paramDTO.setStandardDeviation(param.getStandardDeviation());
                        paramDTO.setLow(param.getLow());
                        paramDTO.setAverage(param.getAverage());
                        paramDTO.setMean(param.getMean());
                        paramDTO.setHigh(param.getHigh());
                        paramDTO.setShape(param.getShape());
                        paramDTO.setScale(param.getScale());
                    }

                    List<ActivityObserverDTO> observers = config.getObservers().stream()
                            .map(obs -> new ActivityObserverDTO(
                                    obs.getId(),
                                    obs.getName(),
                                    obs.getQueue_name(),
                                    obs.getPosition(),
                                    obs.getType()
                            ))
                            .toList();

                    ActivityConfigDTO dto = new ActivityConfigDTO(
                            config.getDeliveryProcess() != null ? config.getDeliveryProcess().getId() : null, // processId
                            config.getDeliveryProcess() != null ? config.getDeliveryProcess().getName() : null, // processName
                            config.getActivity().getId(),
                            config.getId(),
                            config.getActivity().getName(),
                            config.getActivity().getType().name(),
                            config.getActivity().getSuperActivity() != null ? config.getActivity().getSuperActivity().getId() : null,
                            config.getDependencyType(),
                            config.getTimeBox(),
                            config.getConditionToProcess(),
                            config.getProcessingQuantity(),
                            config.getIterationBehavior(),
                            config.getRequiredResources(),
                            config.getDistributionType(),
                            paramDTO,
                            observers
                    );


                    if (config.getDeliveryProcess() != null) {
                        dto.setProcessId(config.getDeliveryProcess().getId());
                        dto.setProcessName(config.getDeliveryProcess().getName());
                    }

                    return dto;
                })
                .toList();

        return dtos;
    }









}

