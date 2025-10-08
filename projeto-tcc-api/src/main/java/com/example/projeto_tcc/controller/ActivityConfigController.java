package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.ActivityConfigDTO;
import com.example.projeto_tcc.dto.ObserverActivityDTO;
import com.example.projeto_tcc.entity.ActivityConfig;
import com.example.projeto_tcc.service.ActivityConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity-configs")
@CrossOrigin(origins = "*") // permitir acesso do front-end
@RequiredArgsConstructor
public class ActivityConfigController {
    private final ActivityConfigService configService;

    // GET um ActivityConfig e todos os seus observers
    @GetMapping("/{id}")
    public ActivityConfigDTO getConfig(@PathVariable Long id) {
        return configService.getActivityConfig(id);
    }

    // GET todos os ActivityConfig de um DeliveryProcess e seus observers
    @GetMapping("/process/{deliveryProcessId}")
    public List<ActivityConfigDTO> getByDeliveryProcess(@PathVariable Long deliveryProcessId) {
        return configService.getActivityByDeliveryProcess(deliveryProcessId);
    }

    // GET todos os observers de um ActivityConfig
    @GetMapping("/observers/{activityConfigId}")
    public List<ObserverActivityDTO> getObserversByConfig(@PathVariable Long activityConfigId) {
        return configService.getObserversByActivityConfig(activityConfigId);
    }

    // PATCH atualiza parâmetros de ActivityConfig
    @PatchMapping("/{id}")
    public ActivityConfigDTO updateConfig(@PathVariable Long id, @RequestBody ActivityConfigDTO dto) {
        return configService.updateActivityConfig(id, dto);
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