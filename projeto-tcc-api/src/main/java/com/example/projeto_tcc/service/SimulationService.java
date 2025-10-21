package com.example.projeto_tcc.service;

import com.example.projeto_tcc.dto.ProcessSummaryDTO;
import com.example.projeto_tcc.dto.SimulationResponseDTO;
import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

            if (sim.getDeliveryProcess() != null && sim.getDeliveryProcess().getWbs() != null
                    && sim.getDeliveryProcess().getWbs().getProcessElements() != null) {
                processCount++;

                processes = sim.getDeliveryProcess().getWbs().getProcessElements().stream()
                        .map(p -> new ProcessSummaryDTO(p.getId(), p.getName()))
                        .toList();
            }

            String status;
            if (sim.getStatus() != null) {
                status = sim.getStatus();
            } else if (sim.getDeliveryProcess() == null) {
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



    public Simulation getSimulation(Long id) {
        return simulationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Simulação não encontrada"));
    }

    public void deleteSimulation(Long id) {
        simulationRepository.deleteById(id);
    }

    public Simulation linkDeliveryProcess(Long simulationId, DeliveryProcess process) {
        Simulation simulation = simulationRepository.findById(simulationId)
                .orElseThrow(() -> new RuntimeException("Simulação não encontrada"));

        simulation.setDeliveryProcess(process);
        return simulationRepository.save(simulation);
    }


}
