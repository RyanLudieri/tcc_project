package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.ProcessSummaryDTO;
import com.example.projeto_tcc.dto.SimulationResponseDTO;
import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.repository.SimulationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

                        // Contagem recursiva de elementos
                        List<ProcessElement> elements = new ArrayList<>(dp.getProcessElements());

                        phases = countTypeRecursive(elements, "PHASE");
                        iterations = countTypeRecursive(elements, "ITERATION");
                        activities = countTypeRecursive(elements, "ACTIVITY");
                        tasks = countChildrenRecursive(elements, "ACTIVITY");

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

            String status = sim.getStatus() != null
                    ? sim.getStatus()
                    : (sim.getProcesses() == null || sim.getProcesses().isEmpty() ? "Empty" : "Setup");

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

    // Função recursiva para contar elementos de um tipo específico
    private int countTypeRecursive(List<ProcessElement> elements, String type) {
        if (elements == null) return 0;
        int count = 0;
        for (ProcessElement e : elements) {
            if (type.equals(e.getType())) count++;
            // Precisa fazer cast para List<ProcessElement> porque Activity.getChildren() é List<Activity>
            count += countTypeRecursive((List<ProcessElement>)(List<?>) e.getChildren(), type);
        }
        return count;
    }

    // Função recursiva para contar filhos de certo tipo
    private int countChildrenRecursive(List<ProcessElement> elements, String type) {
        if (elements == null) return 0;
        int count = 0;
        for (ProcessElement e : elements) {
            if (type.equals(e.getType()) && e.getChildren() != null) {
                count += e.getChildren().size();
            }
            count += countChildrenRecursive((List<ProcessElement>)(List<?>) e.getChildren(), type);
        }
        return count;
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

                    if (dp.getPhaseConfigs() != null)
                        phases = dp.getPhaseConfigs().size();

                    if (dp.getIterationConfigs() != null)
                        iterations = dp.getIterationConfigs().size();

                    if (dp.getRoleConfigs() != null)
                        roles = dp.getRoleConfigs().size();

                    if (dp.getWorkProductConfigs() != null)
                        artifacts = dp.getWorkProductConfigs().size();

                    if (dp.getActivityConfigs() != null) {
                        activities = dp.getActivityConfigs().size();

                        tasks = dp.getActivityConfigs().stream()
                                .map(ActivityConfig::getActivity)
                                .filter(a -> a != null && a.getChildren() != null)
                                .flatMap(a -> a.getChildren().stream()
                                        .filter(child ->
                                                child.getSuperActivity() != null &&
                                                        child.getSuperActivity().equals(a)))
                                .mapToInt(child -> 1)
                                .sum();
                    }
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

        // ✅ STATUS
        String status = sim.getStatus() != null
                ? sim.getStatus()
                : (sim.getProcesses() == null || sim.getProcesses().isEmpty() ? "Empty" : "Setup");

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

    public SimulationResponseDTO updateObjective(Long id, String newObjective) {
        Simulation sim = simulationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Simulação não encontrada"));

        sim.setObjective(newObjective);
        simulationRepository.save(sim);

        return getSimulation(id);
    }




}
