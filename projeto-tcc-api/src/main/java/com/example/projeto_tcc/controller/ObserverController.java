package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.entity.Observer;
import com.example.projeto_tcc.repository.ObserverRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/observers")
public class ObserverController {
    private final ObserverRepository observerRepository;

    public ObserverController(ObserverRepository observerRepository) {
        this.observerRepository = observerRepository;
    }

    @PostMapping
    public ResponseEntity<Observer> createObserver(@RequestBody Observer observer) {
        return ResponseEntity.ok(observerRepository.save(observer));
    }
}
