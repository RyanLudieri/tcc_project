package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.entity.WorkProductConfig;
import com.example.projeto_tcc.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import com.example.projeto_tcc.dto.SimulationCreateDTO;
import com.example.projeto_tcc.entity.DeliveryProcess;
import com.example.projeto_tcc.entity.Simulation;

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

    @PostMapping("/execute")
    public ResponseEntity<String> executeSimulation(
            @RequestParam float simulationDuration,
            @RequestParam(defaultValue = "1") Integer replications) {

        try {
            // Chama o 'execute' que faz o loop
            executionService.executeSimulation(simulationDuration, replications);
            return ResponseEntity.ok("Execução de " + replications + " replicações concluída.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Falha na execução: " + e.getMessage());
        }
    }

    @GetMapping("/get_generated_code")
    public ResponseEntity<String> getGeneratedCode() {
        String javaCode = executionService.getGeneratedJavaCode();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=utf-8")
                .body(javaCode);
    }

    @PostMapping("/get_results")
    public ResponseEntity<String> getResults() {
        try {
            // 1. Pega o ID do processo que está ativo na sessão
            Long processId = executionService.getActiveProcessId();
            if (processId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Erro: Nenhuma simulação foi compilada nesta sessão. Chame /generate-and-compile primeiro.");
            }

            // 2. BUSCA A LISTA ATUALIZADA (com 'variableType') DO BANCO DE DADOS
            List<WorkProductConfig> configList = workProductConfigService.findAllByDeliveryProcessId(processId);

            // 3. Passa a lista (do DB) para o ExecutionService (da sessão)
            String results = executionService.getFilteredResults(configList);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=utf-8")
                    .body(results);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar ou filtrar resultados: " + e.getMessage());
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
