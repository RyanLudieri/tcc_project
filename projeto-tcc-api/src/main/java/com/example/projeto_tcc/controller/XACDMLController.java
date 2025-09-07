package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.entity.XACDMLFile;
import com.example.projeto_tcc.repository.XACDMLFileRepository;
import com.example.projeto_tcc.service.XACDMLService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequestMapping("/xacdml")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // permite chamadas do frontend
public class XACDMLController {

    private final XACDMLFileRepository xacdmlRepo;
    private final XACDMLService xacdmlService;

    @PostMapping("/generate/{processId}")
    public ResponseEntity<String> generate(
            @PathVariable Long processId, @RequestParam String acdId) {
        XACDMLFile file = xacdmlService.generateXACDML(processId, acdId);
        return ResponseEntity.ok(file.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getXacdmlContent(@PathVariable Long id) {
        XACDMLFile file = xacdmlRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("XACDML não encontrado com id: " + id));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML) // ou TEXT_XML
                .body(file.getContent());
    }

    @PostMapping("/generate-file/{processId}")
    public ResponseEntity<String> generateXACDMLFile(
            @PathVariable Long processId,
            @RequestParam String acdId) {

        // Gera o arquivo .xacdml escapando caracteres e salvando em xacdml_output
        Path path = xacdmlService.generateXACDMLFile(processId, acdId);

        return ResponseEntity.ok("Arquivo gerado em: " + path.toAbsolutePath());
    }



}
