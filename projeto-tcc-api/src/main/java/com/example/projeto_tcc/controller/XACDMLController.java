package com.example.projeto_tcc.controller;

import com.example.projeto_tcc.entity.XACDMLFile;
import com.example.projeto_tcc.service.XACDMLService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/xacdml")
@RequiredArgsConstructor
public class XACDMLController {

    private final XACDMLService xacdmlService;

    @PostMapping("/generate/{processId}")
    public ResponseEntity<String> generate(@PathVariable Long processId) {
        XACDMLFile file = xacdmlService.generateXACDML(processId);
        return ResponseEntity.ok(file.getContent());
    }

}
