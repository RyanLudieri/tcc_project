package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.ObserverUpdateDTO;
import com.example.projeto_tcc.dto.WorkProductConfigDTO;
import com.example.projeto_tcc.dto.WorkProductConfigGetDTO;
import com.example.projeto_tcc.dto.WorkProductConfigUpdateDTO;
import com.example.projeto_tcc.entity.MethodElementObserver;
import com.example.projeto_tcc.enums.ObserverMethodElementType;
import com.example.projeto_tcc.service.WorkProductConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/work-product-configs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WorkProductConfigController {

    private final WorkProductConfigService workProductConfigService;

    @GetMapping("/process/{deliveryProcessId}")
    public List<WorkProductConfigDTO> getByDeliveryProcess(@PathVariable Long deliveryProcessId) {
        return workProductConfigService.getWorkProductsByDeliveryProcess(deliveryProcessId);
    }

    @GetMapping("/process/{deliveryProcessId}/variables")
    public ResponseEntity<List<WorkProductConfigGetDTO>> getWorkProductVariables(
            @PathVariable("deliveryProcessId") Long processId) {

        List<WorkProductConfigGetDTO> dtoList =
                workProductConfigService.findAllWorkProductConfigs(processId);

        return ResponseEntity.ok(dtoList);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<WorkProductConfigUpdateDTO> updateWorkProductConfig(
            @PathVariable Long id,
            @RequestBody WorkProductConfigUpdateDTO dto) {
        return ResponseEntity.ok(workProductConfigService.updateWorkProductConfig(id, dto));
    }


    @PostMapping("/{workProductConfigId}/observers")
    public ResponseEntity<MethodElementObserver> addObserver(
            @PathVariable Long workProductConfigId,
            @RequestParam(name = "type", required = false) ObserverMethodElementType type) {
        MethodElementObserver observer = workProductConfigService.addObserverToWorkProductConfig(
                workProductConfigId,
                type != null ? type : ObserverMethodElementType.NONE);
        return ResponseEntity.ok(observer);
    }

    @PatchMapping("/observers/{observerId}")
    public ResponseEntity<MethodElementObserver> updateObserver(
            @PathVariable Long observerId,
            @RequestBody ObserverUpdateDTO dto) {
        return ResponseEntity.ok(workProductConfigService.updateObserver(observerId, dto));
    }

    @DeleteMapping("/{id}/observers/{observerId}")
    public ResponseEntity<Void> removeObserver(
            @PathVariable Long id,
            @PathVariable Long observerId) {
        workProductConfigService.removeObserverFromWorkProductConfig(id, observerId);
        return ResponseEntity.noContent().build();
    }

}

