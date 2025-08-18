package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.ObserverUpdateDTO;
import com.example.projeto_tcc.dto.WorkProductConfigDTO;
import com.example.projeto_tcc.entity.MethodElementObserver;
import com.example.projeto_tcc.entity.WorkProductConfig;
import com.example.projeto_tcc.service.WorkProductConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/work-product-configs")
@RequiredArgsConstructor
public class WorkProductConfigController {

    private final WorkProductConfigService service;

    @GetMapping("/process/{deliveryProcessId}")
    public List<WorkProductConfigDTO> getByDeliveryProcess(@PathVariable Long deliveryProcessId) {
        return service.getWorkProductsByDeliveryProcess(deliveryProcessId);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<WorkProductConfigDTO> updateWorkProductConfig(
            @PathVariable Long id,
            @RequestBody WorkProductConfigDTO dto) {
        return ResponseEntity.ok(service.updateWorkProductConfig(id, dto));
    }


    // POST - adicionar observer
    @PostMapping("/{id}/observers")
    public ResponseEntity<MethodElementObserver> addObserver(@PathVariable Long id) {
        return ResponseEntity.ok(service.addObserverToWorkProductConfig(id));
    }

    // PATCH - atualizar observer
    @PatchMapping("/observers/{observerId}")
    public ResponseEntity<MethodElementObserver> updateObserver(
            @PathVariable Long observerId,
            @RequestBody ObserverUpdateDTO dto) {
        return ResponseEntity.ok(service.updateObserver(observerId, dto));
    }

    // DELETE - remover observer
    @DeleteMapping("/{id}/observers/{observerId}")
    public ResponseEntity<Void> removeObserver(
            @PathVariable Long id,
            @PathVariable Long observerId) {
        service.removeObserverFromWorkProductConfig(id, observerId);
        return ResponseEntity.noContent().build();
    }

}

