package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.ObserverUpdateDTO;
import com.example.projeto_tcc.dto.WorkProductConfigDTO;
import com.example.projeto_tcc.entity.MethodElementObserver;
import com.example.projeto_tcc.entity.WorkProductConfig;
import com.example.projeto_tcc.enums.ObserverMethodElementType;
import com.example.projeto_tcc.service.WorkProductConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/work-product-configs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // permite chamadas do frontend
public class WorkProductConfigController {

    private final WorkProductConfigService workProductConfigService;

    @GetMapping("/process/{deliveryProcessId}")
    public List<WorkProductConfigDTO> getByDeliveryProcess(@PathVariable Long deliveryProcessId) {
        return workProductConfigService.getWorkProductsByDeliveryProcess(deliveryProcessId);
    }




    @PatchMapping("/{id}")
    public ResponseEntity<WorkProductConfigDTO> updateWorkProductConfig(
            @PathVariable Long id,
            @RequestBody WorkProductConfigDTO dto) {
        return ResponseEntity.ok(workProductConfigService.updateWorkProductConfig(id, dto));
    }


    // POST - adicionar observer
    @PostMapping("/{workProductConfigId}/observers")
    public ResponseEntity<MethodElementObserver> addObserver(
            @PathVariable Long workProductConfigId,
            @RequestParam(name = "type", required = false) ObserverMethodElementType type) {
        MethodElementObserver observer = workProductConfigService.addObserverToWorkProductConfig(
                workProductConfigId,
                type != null ? type : ObserverMethodElementType.NONE);
        return ResponseEntity.ok(observer);
    }

    // PATCH - atualizar observer
    @PatchMapping("/observers/{observerId}")
    public ResponseEntity<MethodElementObserver> updateObserver(
            @PathVariable Long observerId,
            @RequestBody ObserverUpdateDTO dto) {
        return ResponseEntity.ok(workProductConfigService.updateObserver(observerId, dto));
    }

    // DELETE - remover observer
    @DeleteMapping("/{id}/observers/{observerId}")
    public ResponseEntity<Void> removeObserver(
            @PathVariable Long id,
            @PathVariable Long observerId) {
        workProductConfigService.removeObserverFromWorkProductConfig(id, observerId);
        return ResponseEntity.noContent().build();
    }

}

