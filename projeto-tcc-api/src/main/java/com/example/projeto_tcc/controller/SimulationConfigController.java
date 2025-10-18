package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.GeneratorConfigDTO;
import com.example.projeto_tcc.dto.GeneratorConfigRequestDTO;
import com.example.projeto_tcc.entity.GeneratorConfig;
import com.example.projeto_tcc.service.WorkProductConfigService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simulation-config")
@RequiredArgsConstructor
public class SimulationConfigController {

    private final WorkProductConfigService configService;

    @PostMapping("/process/{processId}/generators")
    public ResponseEntity<GeneratorConfigDTO> addGenerator(
            @PathVariable Long processId,
            @RequestBody GeneratorConfigRequestDTO requestDto) {

        GeneratorConfigDTO responseDto = configService.addGeneratorToProcess(processId, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/generators/{generatorId}")
    public ResponseEntity<GeneratorConfigDTO> updateGenerator(
            @PathVariable Long generatorId,
            @RequestBody GeneratorConfigRequestDTO requestDto) {
        try {
            GeneratorConfigDTO updatedDto = configService.updateGenerator(generatorId, requestDto);
            return ResponseEntity.ok(updatedDto);
        } catch (EntityNotFoundException e) {
            // Se o gerador com o ID fornecido não for encontrado
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // Outros erros inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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