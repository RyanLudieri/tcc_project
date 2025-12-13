package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.ActivityConfigDTO;
import com.example.projeto_tcc.dto.ObserverActivityDTO;
import com.example.projeto_tcc.service.ActivityConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity-configs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ActivityConfigController {
    private final ActivityConfigService configService;

    /**
     * GET um ActivityConfig e seus Observers
     * @param id
     * @return um ActivityConfig e seus Observers
     */
    @GetMapping("/{id}")
    public ActivityConfigDTO getConfig(@PathVariable Long id) {
        return configService.getActivityConfig(id);
    }

    /**
     * GET todos os ActivityConfig de um DeliveryProcess e seus Observers
     * @param deliveryProcessId
     * @return todos os ActivityConfig de um DeliveryProcess e seus Observers
     */
    @GetMapping("/process/{deliveryProcessId}")
    public List<ActivityConfigDTO> getByDeliveryProcess(@PathVariable Long deliveryProcessId) {
        return configService.getActivityByDeliveryProcess(deliveryProcessId);
    }

    /**
     * GET Observers de um ActivityConfig
     * @param activityConfigId
     * @return  todos os Observers de um ActivityConfig
     */
    @GetMapping("/observers/{activityConfigId}")
    public List<ObserverActivityDTO> getObserversByConfig(@PathVariable Long activityConfigId) {
        return configService.getObserversByActivityConfig(activityConfigId);
    }

    /**
     * PATCH de parâmetros de ActivityConfig
     * @param id
     * @param dto
     */
    @PatchMapping("/{id}")
    public ActivityConfigDTO updateConfig(@PathVariable Long id, @RequestBody ActivityConfigDTO dto) {
        return configService.updateActivityConfig(id, dto);
    }

    /**
     * POST de Observer em um ActivityConfig
     * @param activityConfigId
     * @return Observer criado
     */
    @PostMapping("/observers/{activityConfigId}")
    public ObserverActivityDTO addObserver(@PathVariable Long activityConfigId) {
        return configService.addObserver(activityConfigId);
    }

    /**
     * PATCH de um Observer
     * @param observerId
     * @param dto (body)
     */
    @PatchMapping("/observers/{observerId}")
    public ObserverActivityDTO updateObserver(@PathVariable Long observerId, @RequestBody ObserverActivityDTO dto) {
        return configService.updateObserver(observerId, dto);
    }

    /**
     * DELETE um Observer
     * @param observerId
     */
    @DeleteMapping("/observers/{observerId}")
    public void deleteObserver(@PathVariable Long observerId) {
        configService.removeObserver(observerId);
    }
}