package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.entity.MethodElementObserver;
import com.example.projeto_tcc.service.RoleConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/{roleConfigId}/observers/{observerId}")
    public ResponseEntity<Void> removeObserver(
            @PathVariable Long roleConfigId,
            @PathVariable Long observerId) {
        roleConfigService.removeObserverFromRoleConfig(roleConfigId, observerId);
        return ResponseEntity.noContent().build();
    }


}

