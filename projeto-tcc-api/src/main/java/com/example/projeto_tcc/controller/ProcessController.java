package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.dto.ProcessDTO;
import com.example.projeto_tcc.entity.Process;
import com.example.projeto_tcc.service.ProcessService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/process")
@CrossOrigin(origins = "*") // permitir acesso do front-end
public class ProcessController {

    private final ProcessService service;

    public ProcessController(ProcessService service) {
        this.service = service;
    }

    @PostMapping
    public Process createProcess(@RequestBody ProcessDTO dto) {
        return service.saveProcess(dto);
    }

    @GetMapping
    public List<Process> getAllProcesses() {
        return service.getAllProcesses();
    }
}