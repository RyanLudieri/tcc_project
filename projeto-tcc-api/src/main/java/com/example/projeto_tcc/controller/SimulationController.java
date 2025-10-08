package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.service.SimulationGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

/**
 * Controller principal para gerenciar a geração de simulações.
 * Este é o ponto de entrada para o fluxo completo:
 * 1. Gerar XACDML -> 2. Transformar XSLT -> 3. Salvar arquivo .java
 */
@RestController
@RequestMapping("/simulations") // Rota principal da aplicação
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SimulationController {

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
}