package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.model.DeliveryProcess;
import com.example.projeto_tcc.service.DeliveryProcessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/delivery-process")
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

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryProcess> atualizar(@PathVariable Long id, @RequestBody DeliveryProcess processo) {
        DeliveryProcess updatedProcess = service.atualizar(id, processo);
        return ResponseEntity.ok(updatedProcess);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // Retorna HTTP 204 No Content
    }

}
