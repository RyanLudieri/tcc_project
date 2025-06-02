package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.*;
import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.repository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SampleService {

    private final SampleRepository sampleRepository;
    private final DistributionParameterRepository parameterRepository;
    private final ActivityRepository activityRepository;

    public SampleService(SampleRepository sampleRepository,
                         DistributionParameterRepository parameterRepository,
                         ActivityRepository activityRepository) {
        this.sampleRepository = sampleRepository;
        this.parameterRepository = parameterRepository;
        this.activityRepository = activityRepository;
    }

    public SampleDTO createSample(SampleDTO dto) {
        // Persistir ou associar parâmetros
        DistributionParameter param = dto.getParameter();
        if (param.getId() == null) {
            param = parameterRepository.save(param);
        } else {
            param = parameterRepository.findById(param.getId())
                    .orElseThrow(() -> new RuntimeException("Parameter não encontrado"));
        }

        Sample sample = new Sample();
        sample.setName(dto.getName());
        sample.setSize(dto.getSize());
        sample.setDistribution(dto.getDistribution());
        sample.setParameter(param);

        List<DurationMeasurement> measurements = new ArrayList<>();
        if (dto.getMeasurements() != null) {
            for (DurationMeasurementDTO mDto : dto.getMeasurements()) {
                DurationMeasurement dm = new DurationMeasurement();
                dm.setName(mDto.getName());
                dm.setValue(mDto.getValue());

                if (mDto.getActivity() != null) {
                    Activity activity = activityRepository.findById(mDto.getActivity().getId())
                            .orElseThrow(() -> new RuntimeException("Activity não encontrada"));
                    dm.setActivity(activity);
                }

                dm.setSample(sample);
                measurements.add(dm);
            }
        }

        sample.setMeasurements(measurements);
        Sample saved = sampleRepository.save(sample);

        return convertToDTO(saved);
    }

    private SampleDTO convertToDTO(Sample sample) {
        List<DurationMeasurementDTO> measurementDTOs = sample.getMeasurements().stream().map(dm -> {
            Activity act = dm.getActivity();
            ActivitySummaryDTO actDTO = act != null
                    ? new ActivitySummaryDTO(act.getId(), act.getName(), act.getType())
                    : null;

            return new DurationMeasurementDTO(dm.getId(), dm.getName(), dm.getValue(), actDTO);
        }).toList();

        return new SampleDTO(
                sample.getId(),
                sample.getName(),
                sample.getSize(),
                sample.getDistribution(),
                sample.getParameter(),
                measurementDTOs
        );
    }
}

