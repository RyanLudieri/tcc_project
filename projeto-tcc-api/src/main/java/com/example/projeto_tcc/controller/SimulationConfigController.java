package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.GenerateObserverDTO;
import com.example.projeto_tcc.dto.GenerateObserverRequestDTO;
import com.example.projeto_tcc.dto.GeneratorConfigDTO;
import com.example.projeto_tcc.dto.GeneratorConfigRequestDTO;
import com.example.projeto_tcc.service.WorkProductConfigService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/simulation-config")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
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
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/generators/{generatorId}")
    public ResponseEntity<Void> removeGenerator(@PathVariable Long generatorId) {
        try {
            configService.removeGenerator(generatorId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/work-product/{workProductConfigId}/set-as-destroyer")
    public ResponseEntity<Void> setAsDestroyer(
            @PathVariable Long workProductConfigId,
            @RequestParam boolean isDestroyer) {
        configService.setDestroyer(workProductConfigId, isDestroyer);
        return ResponseEntity.ok().build();
    }

    // --- NOVO: GET PARA LISTAR GERADORES POR PROCESSO ---
    @GetMapping("/process/{processId}/generators")
    public ResponseEntity<List<GeneratorConfigDTO>> getGeneratorsByProcess(@PathVariable Long processId) {
        List<GeneratorConfigDTO> generators = configService.getGeneratorsByProcess(processId);
        return ResponseEntity.ok(generators);
    }

    // --- NOVO: GET PARA BUSCAR UM GERADOR ESPECÍFICO POR ID ---
    @GetMapping("/generators/{generatorId}")
    public ResponseEntity<GeneratorConfigDTO> getGeneratorById(@PathVariable Long generatorId) {
        try {
            GeneratorConfigDTO generator = configService.getGeneratorById(generatorId);
            return ResponseEntity.ok(generator);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET: Lista todos os observers de um gerador específico.
     */
    @GetMapping("/generators/{generatorId}/observers")
    public ResponseEntity<List<GenerateObserverDTO>> getObserversByGenerator(@PathVariable Long generatorId) {
        try {
            List<GenerateObserverDTO> observers = configService.getObserversByGenerator(generatorId);
            return ResponseEntity.ok(observers);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST: Cria um NOVO observer padrão para um gerador.
     */
    @PostMapping("/generators/{generatorId}/observers")
    public ResponseEntity<GenerateObserverDTO> addGeneratorObserver(
            @PathVariable Long generatorId,
            @RequestBody GenerateObserverRequestDTO request
    ) {
        try {
            GenerateObserverDTO newObserver = configService.addGeneratorObserver(generatorId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(newObserver);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    /**
     * PATCH: Atualiza um observer específico pelo ID dele.
     */
    @PatchMapping("/generator-observers/{observerId}")
    public ResponseEntity<GenerateObserverDTO> updateGeneratorObserver(
            @PathVariable Long observerId,
            @RequestBody GenerateObserverDTO dto) {
        try {
            GenerateObserverDTO updatedObserver = configService.updateGeneratorObserver(observerId, dto);
            return ResponseEntity.ok(updatedObserver);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE: Remove um observer específico pelo ID dele.
     */
    @DeleteMapping("/generator-observers/{observerId}")
    public ResponseEntity<Void> deleteGeneratorObserver(@PathVariable Long observerId) {
        try {
            configService.deleteGeneratorObserver(observerId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

}