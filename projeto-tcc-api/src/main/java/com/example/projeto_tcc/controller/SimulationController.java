package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.SimulationCreateDTO;
import com.example.projeto_tcc.dto.SimulationResponseDTO;
import com.example.projeto_tcc.entity.DeliveryProcess;
import com.example.projeto_tcc.entity.GlobalSimulationResult;
import com.example.projeto_tcc.entity.Simulation;
import com.example.projeto_tcc.entity.WorkProductConfig;
import com.example.projeto_tcc.repository.WorkProductConfigRepository;
import com.example.projeto_tcc.service.*;
//import com.example.projeto_tcc.service.SimulationGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/simulations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SimulationController {

    private final SimulationService simulationService;
    private final DeliveryProcessService deliveryProcessService;
    private final SimulationGenerationService simulationGenerationService;

    @Autowired
    private WorkProductConfigRepository workProductConfigRepository;

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private WorkProductConfigService workProductConfigService;

    @PostMapping("/generate_and_compile/{processId}")
    public ResponseEntity<String> generateAndCompileSimulation(
            @PathVariable Long processId,
            @RequestParam(defaultValue = "SimulationRun") String acdId) {
        try {
            String uniqueAcdId = acdId + "_" + processId + "_" + System.currentTimeMillis();
            Path generatedFilePath = simulationGenerationService.generateSimulation(processId, uniqueAcdId);
            String javaCode = new String(Files.readAllBytes(generatedFilePath));

            // "Compila" (prepara) a sessão de execução
            executionService.compile(javaCode, "DynamicExperimentationProgramProxy",processId);

            return ResponseEntity.ok("Simulação gerada e sessão preparada.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Falha ao gerar ou compilar: " + e.getMessage());
        }
    }

    @PostMapping("/execute/{processId}")
    public ResponseEntity<Map<String, Long>> executeSimulation(
            @PathVariable Long processId,
            @RequestParam float simulationDuration,
            @RequestParam(defaultValue = "1") Integer replications) {

        try {
            List<WorkProductConfig> configList = workProductConfigRepository.findByDeliveryProcessId(processId);

            GlobalSimulationResult result = executionService.executeSimulation(
                    processId,
                    simulationDuration,
                    replications,
                    configList);

            Map<String, Long> response = new HashMap<>();

            if (result != null) {
                response.put("executionId", result.getId());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.internalServerError().build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/generated-code/{processId}")
    public ResponseEntity<String> getGeneratedCode(@PathVariable Long processId) {

        String javaCode = executionService.generateCodeForPreview(processId);

        if (javaCode == null || javaCode.startsWith("Erro")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(javaCode != null ? javaCode : "Erro desconhecido ao gerar código.");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=utf-8")
                .body(javaCode);
    }

    @GetMapping("/get_results")
    public ResponseEntity<String> getResults() {
        try {
            // 1. Recupera o ID do processo ativo na sessão do service
            Long processId = executionService.getActiveProcessId();
            if (processId == null) {
                return ResponseEntity.badRequest().body("Nenhum processo ativo ou simulação não compilada.");
            }

            // 2. Busca a configuração para saber quais filas calcular estatísticas globais
            List<WorkProductConfig> configs = workProductConfigRepository.findAllByDeliveryProcessId(processId);

            // 3. Gera as duas partes do relatório
            String detalhado = executionService.getDetailedSimulationLog();
            String global = executionService.getFilteredResults(configs);

            // 4. Concatena e retorna
            String relatorioFinal = detalhado + "\n" + global;

            return ResponseEntity.ok(relatorioFinal);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro ao gerar relatório: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Simulation> createSimulation(@RequestBody SimulationCreateDTO dto) {
        Simulation simulation = new Simulation();
        simulation.setObjective(dto.getObjective());
        return ResponseEntity.ok(simulationService.createSimulation(simulation));
    }

    @GetMapping
    public List<SimulationResponseDTO> getAllSimulations() {
        return simulationService.getAllSimulations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SimulationResponseDTO> getSimulation(@PathVariable Long id) {
        SimulationResponseDTO simulationDto = simulationService.getSimulation(id);
        return ResponseEntity.ok(simulationDto);
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

    @PatchMapping("/{id}/objective")
    public ResponseEntity<SimulationResponseDTO> updateObjective(
            @PathVariable Long id,
            @RequestBody SimulationCreateDTO dto) {

        SimulationResponseDTO updated = simulationService.updateObjective(id, dto.getObjective());
        return ResponseEntity.ok(updated);
    }




}

