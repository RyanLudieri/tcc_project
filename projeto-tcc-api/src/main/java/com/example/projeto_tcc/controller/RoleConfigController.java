package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.ObserverUpdateDTO;
import com.example.projeto_tcc.entity.MethodElementObserver;
import com.example.projeto_tcc.entity.RoleConfig;
import com.example.projeto_tcc.service.RoleConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role-configs")
@RequiredArgsConstructor
public class RoleConfigController {

    private final RoleConfigService roleConfigService;

    @PostMapping("/{roleConfigId}/observers")
    public ResponseEntity<MethodElementObserver> addObserver(@PathVariable Long roleConfigId) {
        MethodElementObserver observer = roleConfigService.addObserverToRoleConfig(roleConfigId);
        return ResponseEntity.ok(observer);
    }

    @PatchMapping("/observers/{id}")
    public ResponseEntity<MethodElementObserver> updateObserver(
            @PathVariable Long id,
            @RequestBody ObserverUpdateDTO dto) {
        MethodElementObserver updated = roleConfigService.updateObserver(id, dto);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{roleConfigId}/observers/{observerId}")
    public ResponseEntity<Void> removeObserver(
            @PathVariable Long roleConfigId,
            @PathVariable Long observerId) {
        roleConfigService.removeObserverFromRoleConfig(roleConfigId, observerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/process/{deliveryProcessId}")
    public ResponseEntity<List<RoleConfig>> getRoleConfigsByProcess(@PathVariable Long deliveryProcessId) {
        List<RoleConfig> roles = roleConfigService.getRolesByDeliveryProcess(deliveryProcessId);
        return ResponseEntity.ok(roles);
    }


}

