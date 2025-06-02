package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.SampleDTO;
import com.example.projeto_tcc.entity.Sample;
import com.example.projeto_tcc.service.SampleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/samples")
public class SampleController {
    private final SampleService sampleService;

    public SampleController(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    @PostMapping
    public ResponseEntity<SampleDTO> createSample(@RequestBody SampleDTO dto) {
        SampleDTO created = sampleService.createSample(dto);
        return ResponseEntity.ok(created);
    }
}
