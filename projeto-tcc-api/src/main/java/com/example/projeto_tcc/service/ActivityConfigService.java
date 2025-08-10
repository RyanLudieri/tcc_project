package com.example.projeto_tcc.service;

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
    private final SampleRepository sampleRepository;
    private final DistributionParameterRepository parameterRepository;
    private final DurationMeasurementRepository measurementRepository;
    private final ObserverRepository observerRepository;
    private final ActivityRepository activityRepository;

    @Transactional
    public ActivityConfig createDefaultConfig(Activity activity) {
        ActivityConfig config = new ActivityConfig();
        config.setActivity(activity);

        // Chama função auxiliar para setar atributos padrões específicos por tipo
        setDefaultAttributesByActivityType(activity, config);

        // 🔹 Define a posição do novo observer
        int position = 1;
        if (config.getObservers() != null && !config.getObservers().isEmpty()) {
            position = config.getObservers().size() + 1;
        }

        // 🔹 Cria e adiciona o Observer padrão
        ActivityObserver observer = new ActivityObserver();
        observer.setPosition(position);
        observer.setQueue_name(activity.getName());
        observer.setName(activity.getName() + " Observer " + position);
        observer.setType(ObserverActivityType.ACTIVE);
        observer.setActivityConfig(config);
        config.getObservers().add(observer);

        // 🟨 Verifica se é um tipo que deve receber medições (apenas TaskDescriptor)
        boolean isTask = activity instanceof TaskDescriptor;
        double defaultValue = isTask ? 480.0 : 0.0;
        int sampleSize = isTask ? 30 : 0;


        // 🔹 Parâmetros de distribuição
        config.setDistributionType(BestFitDistribution.CONSTANT);
        DistributionParameter param = new DistributionParameter();
        param.setConstant(defaultValue);
        parameterRepository.save(param);
        config.setDistributionParameter(param);

        // 🔹 Cria Sample (mesmo que vazio)
        Sample sample = new Sample();
        sample.setName("Sample for " + activity.getName());
        sample.setDistribution(BestFitDistribution.CONSTANT);
        sample.setParameter(param);
        sample.setSize(sampleSize);
        sampleRepository.save(sample);
        config.setSample(sample);

        // 🔹 Medições (só se houver sample > 0)
        List<DurationMeasurement> measurements = new ArrayList<>();
        if (sampleSize > 0) {
            measurements = MeasurementFactory
                    .createConstantDurationMeasurements(defaultValue, sampleSize, activity.getTimeScale());
            for (DurationMeasurement m : measurements) {
                m.setActivity(activity);
                m.setSample(sample);
            }
            measurementRepository.saveAll(measurements);
        }
        sample.setMeasurements(measurements);

        // 🔹 Persistir tudo
        configRepository.save(config);
        activityRepository.save(activity);

        return config;
    }




    public void createDefaultConfigsRecursively(Activity activity) {
        if (!(activity instanceof DeliveryProcess)) {
            createDefaultConfig(activity);
        }

        if (activity.getChildren() != null) {
            for (Activity child : activity.getChildren()) {
                createDefaultConfigsRecursively(child);
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



}

