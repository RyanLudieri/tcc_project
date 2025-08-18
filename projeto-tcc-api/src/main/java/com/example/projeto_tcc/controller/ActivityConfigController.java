package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.ActivityConfigDTO;
import com.example.projeto_tcc.dto.ObserverActivityDTO;
import com.example.projeto_tcc.service.ActivityConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity_configs")
@RequiredArgsConstructor
public class ActivityConfigController {
    private final ActivityConfigService configService;

    @GetMapping("/{id}")
    public ActivityConfigDTO getConfig(@PathVariable Long id) {
        return configService.getActivityConfig(id);
    }

    @PatchMapping("/{id}")
    public ActivityConfigDTO updateConfig(@PathVariable Long id, @RequestBody ActivityConfigDTO dto) {
        return configService.updateActivityConfig(id, dto);
    }

    // GET todos os observers de um ActivityConfig
    @GetMapping("/observers/{activityConfigId}")
    public List<ObserverActivityDTO> getObserversByConfig(@PathVariable Long activityConfigId) {
        return configService.getObserversByActivityConfig(activityConfigId);
    }

    // POST cria um novo observer em um ActivityConfig
    @PostMapping("/observers/{activityConfigId}")
    public ObserverActivityDTO addObserver(@PathVariable Long activityConfigId) {
        return configService.addObserver(activityConfigId);
    }

    // PATCH atualiza um observer
    @PatchMapping("/observers/{observerId}")
    public ObserverActivityDTO updateObserver(@PathVariable Long observerId, @RequestBody ObserverActivityDTO dto) {
        return configService.updateObserver(observerId, dto);
    }

    // DELETE remove um observer
    @DeleteMapping("/observers/{observerId}")
    public void deleteObserver(@PathVariable Long observerId) {
        configService.removeObserver(observerId);
    }
}


