package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.entity.XACDMLFile;
import com.example.projeto_tcc.repository.XACDMLFileRepository;
import com.example.projeto_tcc.service.XACDMLService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável por gerenciar o artefato XACDML.
 * Suas funções são gerar, salvar e consultar o conteúdo XML puro.
 * Ideal para a tela de gerenciamento de XACDML.
 */
@RestController
@RequestMapping("/xacdml")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class XACDMLController {

    private final XACDMLService xacdmlService;
    private final XACDMLFileRepository xacdmlRepo;

    /**
     * Endpoint POST para gerar e salvar um novo XACDML.
     * 1. Chama o serviço para gerar o conteúdo XML.
     * 2. Salva a entidade XACDMLFile no banco de dados.
     * 3. Retorna o conteúdo XML gerado para a tela.
     */
    @PostMapping("/generate/{processId}")
    public ResponseEntity<String> generateAndSaveXacdml(
            @PathVariable Long processId,
            @RequestParam(defaultValue = "GeneratedXACDML") String acdId) {

        // Este método já gera o conteúdo E salva a entidade no banco
        XACDMLFile savedFile = xacdmlService.generateXACDML(processId, acdId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(savedFile.getContent());
    }

    /**
     * Endpoint GET para buscar um XACDML previamente salvo no banco de dados.
     */
    @GetMapping("/{id}")
    public ResponseEntity<String> getXacdmlContent(@PathVariable Long id) {
        XACDMLFile file = xacdmlRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("XACDML não encontrado com id: " + id));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(file.getContent());
    }
}