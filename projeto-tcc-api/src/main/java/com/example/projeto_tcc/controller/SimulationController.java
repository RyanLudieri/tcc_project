package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.SimulationCreateDTO;
import com.example.projeto_tcc.dto.SimulationResponseDTO;
import com.example.projeto_tcc.entity.DeliveryProcess;
import com.example.projeto_tcc.entity.Simulation;
import com.example.projeto_tcc.service.DeliveryProcessService;
//import com.example.projeto_tcc.service.SimulationGenerationService;
import com.example.projeto_tcc.service.SimulationService;
import com.example.projeto_tcc.service.WorkProductConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
//    private final SimulationGenerationService simulationGenerationService;

//    @Autowired
//    private ExecutionService executionService;

    @Autowired
    private WorkProductConfigService workProductConfigService;

//    @PostMapping("/generate_and_compile/{processId}")
//    public ResponseEntity<String> generateAndCompileSimulation(
//            @PathVariable Long processId,
//            @RequestParam(defaultValue = "SimulationRun") String acdId) {
//
//        try {
//            String uniqueAcdId = acdId + "_" + processId + "_" + System.currentTimeMillis();
//            Path generatedFilePath = simulationGenerationService.generateSimulation(processId, uniqueAcdId);
//            String javaCode = new String(Files.readAllBytes(generatedFilePath));
//            String fullClassName = "DynamicExperimentationProgramProxy";
//            executionService.compile(javaCode, fullClassName, processId);
//            String message = "Simulação gerada e compilada com sucesso. Pronta para executar.";
//            return ResponseEntity.ok(message);
//        } catch (Exception e) {
//            String errorMessage = "Falha ao gerar ou compilar a simulação: " + e.getMessage();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
//        }
//    }

//    @PostMapping("/execute")
//    public ResponseEntity<String> executeSimulation(@RequestParam float simulationDuration) {
//        try {
//            executionService.execute(simulationDuration);
//            String message = "Execução com duração " + simulationDuration + " concluída com sucesso.";
//            return ResponseEntity.ok(message);
//        } catch (Exception e) {
//            String errorMessage = "Falha na execução: " + e.getMessage();
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
//        }
//    }

//    @GetMapping("/get_generated_code")
//    public ResponseEntity<String> getGeneratedCode() {
//        String javaCode = executionService.getGeneratedJavaCode();
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=utf-8")
//                .body(javaCode);
//    }
//
//    @PostMapping("/get_results")
//    public ResponseEntity<String> getResults() {
//        try {
//            Long processId = executionService.getActiveProcessId();
//            if (processId == null) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body("Erro: Nenhuma simulação foi compilada nesta sessão.");
//            }
//
//            List<WorkProductConfig> configList = workProductConfigService.findAllByDeliveryProcessId(processId);
//            String results = executionService.getFilteredResults(configList);
//
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=utf-8")
//                    .body(results);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Erro ao buscar ou filtrar resultados: " + e.getMessage());
//        }
//    }

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
}
