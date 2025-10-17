package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.GeneratorConfigDTO;
import com.example.projeto_tcc.entity.GeneratorConfig;
import com.example.projeto_tcc.service.WorkProductConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simulation-config")
@RequiredArgsConstructor
public class SimulationConfigController {

    private final WorkProductConfigService configService;

    @PostMapping("/process/{processId}/generators")
    public ResponseEntity<GeneratorConfig> addGenerator(
            @PathVariable Long processId,
            @RequestBody GeneratorConfigDTO dto) {
        GeneratorConfig newGenerator = configService.addGeneratorToProcess(processId, dto);
        return ResponseEntity.ok(newGenerator);
    }

    @DeleteMapping("/generators/{generatorId}")
    public ResponseEntity<Void> removeGenerator(@PathVariable Long generatorId) {
        configService.removeGenerator(generatorId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/work-product/{workProductConfigId}/set-as-destroyer")
    public ResponseEntity<Void> setAsDestroyer(
            @PathVariable Long workProductConfigId,
            @RequestParam boolean isDestroyer) {
        configService.setDestroyer(workProductConfigId, isDestroyer);
        return ResponseEntity.ok().build();
    }
}