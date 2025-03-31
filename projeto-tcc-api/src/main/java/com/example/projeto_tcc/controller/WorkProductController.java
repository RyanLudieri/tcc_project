package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.model.WorkProduct;
import com.example.projeto_tcc.service.WorkProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-products")
public class WorkProductController {
    private final WorkProductService service;

    public WorkProductController(WorkProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<WorkProduct> createWorkProduct(@RequestBody WorkProduct workProduct) {
        return ResponseEntity.ok(service.createWorkProduct(workProduct));
    }

    @GetMapping
    public ResponseEntity<List<WorkProduct>> getAllWorkProducts() {
        return ResponseEntity.ok(service.getAllWorkProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkProduct> getWorkProductById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getWorkProductById(id));
    }
}
