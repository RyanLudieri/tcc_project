package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.*;
import com.example.projeto_tcc.entity.Activity;
import com.example.projeto_tcc.service.SimulationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PatchMapping("/roles/grouped/{processId}")
    public ResponseEntity<?> patchGroupedRole(
            @PathVariable Long processId,
            @RequestBody GroupedRoleDTO dto) {
        simulationService.patchGroupedRoleFields(processId, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{processId}/roles/grouped")
    public List<GroupedRoleDTO> getGroupedRolesByProcess(@PathVariable Long processId) {
        return simulationService.getGroupedRolesByProcessId(processId);
    }



    @PostMapping("/workProduct")
    public ResponseEntity<WorkProductResponseDTO> createWorkProduct(@RequestBody WorkProductDTO dto) {
        WorkProductResponseDTO created = simulationService.mapWorkProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/workProduct")
    public ResponseEntity<List<WorkProductResponseDTO>> getBeginEndWorkProducts() {
        List<WorkProductResponseDTO> result = simulationService.getAllBeginEndWorkProducts();
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/workProduct/{workProductId}")
    public ResponseEntity<WorkProductResponseDTO> updateWorkProduct(
            @PathVariable Long workProductId,
            @RequestBody WorkProductDTO dto
    ) {
        dto.setWorkProductId(workProductId);
        WorkProductResponseDTO updated = simulationService.mapWorkProductFields(dto);
        return ResponseEntity.ok(updated);
    }







}

