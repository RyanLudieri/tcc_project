package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.entity.Sample;
import com.example.projeto_tcc.repository.SampleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/samples")
public class SampleController {
    private final SampleRepository sampleRepository;

    public SampleController(SampleRepository sampleRepository) {
        this.sampleRepository = sampleRepository;
    }

    @PostMapping
    public ResponseEntity<Sample> createSample(@RequestBody Sample sample) {
        return ResponseEntity.ok(sampleRepository.save(sample));
    }
}
