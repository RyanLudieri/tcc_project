package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.SimulationCreateDTO;
import com.example.projeto_tcc.entity.DeliveryProcess;
import com.example.projeto_tcc.entity.Simulation;
import com.example.projeto_tcc.service.DeliveryProcessService;
import com.example.projeto_tcc.service.SimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/simulations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SimulationController {

    private final SimulationService simulationService;
    private final DeliveryProcessService deliveryProcessService;

    @PostMapping
    public ResponseEntity<Simulation> createSimulation(@RequestBody SimulationCreateDTO dto) {
        Simulation simulation = new Simulation();
        simulation.setObjective(dto.getObjective());
        return ResponseEntity.ok(simulationService.createSimulation(simulation));
    }


    @GetMapping
    public ResponseEntity<List<Simulation>> getAllSimulations() {
        return ResponseEntity.ok(simulationService.getAllSimulations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Simulation> getSimulation(@PathVariable Long id) {
        return ResponseEntity.ok(simulationService.getSimulation(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSimulation(@PathVariable Long id) {
        simulationService.deleteSimulation(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{simulationId}/delivery-process/{processId}")
    public ResponseEntity<Simulation> linkDeliveryProcess(
            @PathVariable Long simulationId,
            @PathVariable Long processId) {

        DeliveryProcess process = deliveryProcessService.getById(processId);
        Simulation updated = simulationService.linkDeliveryProcess(simulationId, process);
        return ResponseEntity.ok(updated);
    }


}
