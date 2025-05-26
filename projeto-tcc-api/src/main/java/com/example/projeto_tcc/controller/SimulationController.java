package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.SimulationParamsDTO;
import com.example.projeto_tcc.service.SimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/simulation")
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/parameters")
    public ResponseEntity<String> setSimulationParameters(@RequestBody SimulationParamsDTO dto) {
        simulationService.setSimulationParameters(dto);
        return ResponseEntity.ok("Simulation parameters set successfully");
    }
}
