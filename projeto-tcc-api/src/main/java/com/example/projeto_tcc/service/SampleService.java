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

    // Construtor com injeção dos repositórios necessários
    public SampleService(SampleRepository sampleRepository,
                         DistributionParameterRepository parameterRepository,
                         ActivityRepository activityRepository) {
        this.sampleRepository = sampleRepository;
        this.parameterRepository = parameterRepository;
        this.activityRepository = activityRepository;
    }

    /**
     * Cria uma nova amostra (Sample) a partir dos dados do DTO.
     * - Persiste o parâmetro de distribuição, se necessário.
     * - Cria medições de duração associadas, relacionando com atividades se informadas.
     * - Salva a amostra completa no banco.
     *
     * @param dto Dados da amostra a ser criada.
     * @return DTO com a amostra salva e seus dados.
     */
    public SampleDTO createSample(SampleDTO dto) {
        // Persiste o parâmetro de distribuição caso não possua ID (novo)
        DistributionParameter param = dto.getParameter();
        if (param.getId() == null) {
            param = parameterRepository.save(param);
        } else {
            param = parameterRepository.findById(param.getId())
                    .orElseThrow(() -> new RuntimeException("Parameter não encontrado"));
        }

        // Cria a entidade Sample com dados do DTO
        Sample sample = new Sample();
        sample.setName(dto.getName());
        sample.setSize(dto.getSize());
        sample.setDistribution(dto.getDistribution());
        sample.setParameter(param);

        // Cria e associa as medições de duração, relacionando com atividades se informadas
        List<DurationMeasurement> measurements = new ArrayList<>();
        if (dto.getMeasurements() != null) {
            for (DurationMeasurementDTO mDto : dto.getMeasurements()) {
                DurationMeasurement dm = new DurationMeasurement();
                dm.setName(mDto.getName());
                dm.setValue(mDto.getValue());

                // Relaciona a atividade associada à medição (se houver)
                if (mDto.getActivity() != null) {
                    Activity activity = activityRepository.findById(mDto.getActivity().getId())
                            .orElseThrow(() -> new RuntimeException("Activity não encontrada"));
                    dm.setActivity(activity);
                }

                dm.setSample(sample);
                measurements.add(dm);
            }
        }

        // Associa medições à amostra
        sample.setMeasurements(measurements);

        // Salva a amostra no banco e retorna o DTO convertido
        Sample saved = sampleRepository.save(sample);
        return convertToDTO(saved);
    }

    /**
     * Converte a entidade Sample para o DTO correspondente,
     * incluindo a conversão dos DurationMeasurements em DTOs com resumo da atividade associada.
     */
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

