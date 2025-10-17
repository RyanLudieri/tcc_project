package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.service.DeliveryProcessService;
import com.example.projeto_tcc.service.SimulationGenerationService;
import com.example.projeto_tcc.service.SimulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import com.example.projeto_tcc.dto.SimulationCreateDTO;
import com.example.projeto_tcc.entity.DeliveryProcess;
import com.example.projeto_tcc.entity.Simulation;

import java.util.List;

@RestController
@RequestMapping("/simulations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SimulationController {

    private final SimulationService simulationService;
    private final DeliveryProcessService deliveryProcessService;

    private final SimulationGenerationService simulationGenerationService;

    /**
     * Endpoint principal que aciona todo o processo de geração do código de simulação.
     * @param processId O ID do processo de entrega a ser simulado.
     * @param acdId Um nome/ID para a execução da simulação (opcional).
     * @return Uma resposta indicando o sucesso e o local do arquivo gerado.
     */
    @PostMapping("/generate/{processId}")
    public ResponseEntity<String> generateSimulation(
            @PathVariable Long processId,
            @RequestParam(defaultValue = "SimulationRun") String acdId) {
        try {
            // Adiciona um timestamp para garantir que o ID seja único a cada execução
            String uniqueAcdId = acdId + "_" + processId + "_" + System.currentTimeMillis();

            // Chama o serviço orquestrador com um único comando
            Path generatedFilePath = simulationGenerationService.generateSimulation(processId, uniqueAcdId);

            String message = "Simulation Manager gerado com sucesso em: " + generatedFilePath.toAbsolutePath();
            return ResponseEntity.ok(message);

        } catch (Exception e) {
            String errorMessage = "Falha ao gerar a simulação: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }



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
