package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.ProcessSummaryDTO;
import com.example.projeto_tcc.dto.SimulationResponseDTO;
import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.repository.SimulationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SimulationService {

    private final SimulationRepository simulationRepository;

    public Simulation createSimulation(Simulation simulation) {
        if (simulation.getObjective() != null && !simulation.getObjective().isBlank()) {
            String obj = simulation.getObjective().trim();
            simulation.setObjective(obj.substring(0, 1).toUpperCase() + obj.substring(1));
        }
        return simulationRepository.save(simulation);
    }

    public List<SimulationResponseDTO> getAllSimulations() {
        List<Simulation> simulations = simulationRepository.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a");

        return simulations.stream().map(sim -> {
            int processCount = 0;
            List<ProcessSummaryDTO> processes = List.of();

            if (sim.getProcesses() != null && !sim.getProcesses().isEmpty()) {
                processCount = sim.getProcesses().size();

                processes = sim.getProcesses().stream().map(p -> {
                    int phases = 0;
                    int iterations = 0;
                    int roles = 0;
                    int activities = 0;
                    int tasks = 0;
                    int artifacts = 0;

                    if (p instanceof DeliveryProcess) {
                        DeliveryProcess dp = (DeliveryProcess) p;

                        // CONTAGEM SIMPLES USANDO processElements do JSON
                        if (dp.getProcessElements() != null) {
                            phases = (int) dp.getProcessElements().stream()
                                    .filter(e -> "PHASE".equals(e.getType()))
                                    .count();

                            iterations = (int) dp.getProcessElements().stream()
                                    .filter(e -> "ITERATION".equals(e.getType()))
                                    .count();

                            activities = (int) dp.getProcessElements().stream()
                                    .filter(e -> "ACTIVITY".equals(e.getType()))
                                    .count();

                            tasks = dp.getProcessElements().stream()
                                    .filter(e -> "ACTIVITY".equals(e.getType()) && e.getChildren() != null)
                                    .mapToInt(e -> e.getChildren().size())
                                    .sum();
                        }

                        if (dp.getRoleConfigs() != null) roles = dp.getRoleConfigs().size();
                        if (dp.getWorkProductConfigs() != null) artifacts = dp.getWorkProductConfigs().size();
                    }

                    String lastModified = sim.getLastModified() != null
                            ? sim.getLastModified().format(formatter)
                            : null;

                    return new ProcessSummaryDTO(
                            p.getId(),
                            p.getName(),
                            phases,
                            iterations,
                            roles,
                            activities,
                            tasks,
                            artifacts,
                            lastModified
                    );
                }).toList();
            }

            String status;
            if (sim.getStatus() != null) {
                status = sim.getStatus();
            } else if (sim.getProcesses() == null || sim.getProcesses().isEmpty()) {
                status = "Empty";
            } else {
                status = "Setup";
            }

            String lastModified = sim.getLastModified() != null
                    ? sim.getLastModified().format(formatter)
                    : null;

            return new SimulationResponseDTO(
                    sim.getId(),
                    sim.getObjective(),
                    processes,
                    processCount,
                    status,
                    lastModified
            );
        }).toList();
    }


    public SimulationResponseDTO getSimulation(Long id) {
        Simulation sim = simulationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Simulação não encontrada"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a");

        int processCount = 0;
        List<ProcessSummaryDTO> processes = List.of();

        if (sim.getProcesses() != null && !sim.getProcesses().isEmpty()) {
            processCount = sim.getProcesses().size();

            processes = sim.getProcesses().stream().map(p -> {
                int phases = 0;
                int iterations = 0;
                int roles = 0;
                int activities = 0;
                int tasks = 0;
                int artifacts = 0;

                if (p instanceof DeliveryProcess) {
                    DeliveryProcess dp = (DeliveryProcess) p;


                    if (dp.getActivityConfigs() != null) {
                        activities = dp.getActivityConfigs().size();
                        tasks = dp.getActivityConfigs().stream()
                                .mapToInt(ac ->
                                        ac.getActivity() != null && ac.getActivity().getChildren() != null
                                                ? ac.getActivity().getChildren().size()
                                                : 0
                                ).sum();
                    }

                    if (dp.getRoleConfigs() != null)
                        roles = dp.getRoleConfigs().size();

                    if (dp.getWorkProductConfigs() != null)
                        artifacts = dp.getWorkProductConfigs().size();

                    if (dp.getPhaseConfigs() != null)
                        phases = dp.getPhaseConfigs().size();

                    // Contagem de iterações novamente
                    if (dp.getGeneratorConfigs() != null)
                        iterations = dp.getGeneratorConfigs().size();
                }

                String lastModified = sim.getLastModified() != null
                        ? sim.getLastModified().format(formatter)
                        : null;

                return new ProcessSummaryDTO(
                        p.getId(),
                        p.getName(),
                        phases,
                        iterations,
                        roles,
                        activities,
                        tasks,
                        artifacts,
                        lastModified
                );
            }).toList();
        }

        String status;
        if (sim.getStatus() != null) {
            status = sim.getStatus();
        } else if (sim.getProcesses() == null || sim.getProcesses().isEmpty()) {
            status = "Empty";
        } else {
            status = "Setup";
        }

        String lastModified = sim.getLastModified() != null
                ? sim.getLastModified().format(formatter)
                : null;

        return new SimulationResponseDTO(
                sim.getId(),
                sim.getObjective(),
                processes,
                processCount,
                status,
                lastModified
        );
    }

    public void deleteSimulation(Long id) {
        simulationRepository.deleteById(id);
    }

    public Simulation linkDeliveryProcess(Long simulationId, DeliveryProcess process) {
        Simulation simulation = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new RuntimeException("Simulação não encontrada"));

        process.setSimulation(simulation);
        simulation.getProcesses().add(process);

        return simulationRepository.save(simulation);
    }
}
