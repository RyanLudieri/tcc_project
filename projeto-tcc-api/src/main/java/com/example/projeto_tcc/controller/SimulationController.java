package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.*;
import com.example.projeto_tcc.entity.Activity;
import com.example.projeto_tcc.entity.Role;
import com.example.projeto_tcc.service.SimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simulation")
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/params")
    public ResponseEntity<ActivityResponseDTO> setSimulationParams(@RequestBody SimulationParamsDTO dto) {
        Activity activity = simulationService.setSimulationParameters(dto);
        ActivityResponseDTO responseDTO = simulationService.toActivityResponseDTO(activity);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/role/{roleId}")
    public ResponseEntity<RoleResponseDTO> mapRole(@PathVariable Long roleId, @RequestBody RoleMappingDTO dto) {
        dto.setRoleId(roleId);
        RoleResponseDTO updated = simulationService.mapRoleFields(dto);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/workProduct/{workProductId}")
    public ResponseEntity<WorkProductResponseDTO> mapWorkProduct(@PathVariable Long workProductId, @RequestBody WorkProductDTO dto) {
        dto.setWorkProductId(workProductId);
        WorkProductResponseDTO updated = simulationService.mapWorkProductFields(dto);
        return ResponseEntity.ok(updated);
    }




}

