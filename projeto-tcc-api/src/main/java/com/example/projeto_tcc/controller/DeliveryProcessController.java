package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.model.DeliveryProcess;
import com.example.projeto_tcc.service.DeliveryProcessService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/delivery-process")
@CrossOrigin("*") // Permite acesso pelo front-end
public class DeliveryProcessController {
    private final DeliveryProcessService service;

    public DeliveryProcessController(DeliveryProcessService service) {
        this.service = service;
    }

    @GetMapping
    public List<DeliveryProcess> getAll() {
        return service.getAll();
    }

    @PostMapping
    public DeliveryProcess create(@RequestBody DeliveryProcess deliveryProcess) {
        return service.create(deliveryProcess);
    }

    @GetMapping("/{id}")
    public DeliveryProcess getById(@PathVariable Long id) {
        return service.getById(id);
    }
}
