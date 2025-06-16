package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.ObserverDTO;
import com.example.projeto_tcc.entity.Observer;
import com.example.projeto_tcc.service.ObserverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/observers")
public class ObserverController {

    private final ObserverService observerService;

    public ObserverController(ObserverService observerService) {
        this.observerService = observerService;
    }

    @PostMapping
    public ResponseEntity<ObserverDTO> createObserver(@RequestBody Observer observer) {
        ObserverDTO dto = observerService.createObserver(observer);
        return ResponseEntity.ok(dto);
    }
}
