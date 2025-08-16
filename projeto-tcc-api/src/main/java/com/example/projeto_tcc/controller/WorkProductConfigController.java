package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.WorkProductConfigDTO;
import com.example.projeto_tcc.entity.WorkProductConfig;
import com.example.projeto_tcc.service.WorkProductConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/work-product-configs")
@RequiredArgsConstructor
public class WorkProductConfigController {

    private final WorkProductConfigService workProductConfigService;

    @GetMapping("/process/{deliveryProcessId}")
    public List<WorkProductConfigDTO> getByDeliveryProcess(@PathVariable Long deliveryProcessId) {
        return workProductConfigService.getWorkProductsByDeliveryProcess(deliveryProcessId);
    }
}

