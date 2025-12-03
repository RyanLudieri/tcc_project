package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.SimulationResultDTO;
import com.example.projeto_tcc.service.ResultsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/results")
public class ResultsController {

    @Autowired
    private ResultsService resultsService;

    @GetMapping("/{executionId}")
    public ResponseEntity<SimulationResultDTO> getResult(@PathVariable Long executionId) {

        SimulationResultDTO result = resultsService.getResultById(executionId);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }
}
