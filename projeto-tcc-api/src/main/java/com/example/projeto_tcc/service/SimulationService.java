package com.example.projeto_tcc.service;

import com.example.projeto_tcc.entity.*;
import com.example.projeto_tcc.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SimulationService {

    private final SimulationRepository simulationRepository;

    public Simulation createSimulation(Simulation simulation) {
        return simulationRepository.save(simulation);
    }

    public List<Simulation> getAllSimulations() {
        return simulationRepository.findAll();
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
