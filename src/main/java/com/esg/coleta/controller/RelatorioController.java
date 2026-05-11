package com.esg.coleta.controller;

import com.esg.coleta.dto.RelatorioESGDTO;
import com.esg.coleta.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorio")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService service;

    @GetMapping("/esg")
    public ResponseEntity<RelatorioESGDTO> gerarRelatorioESG() {
        return ResponseEntity.ok(service.gerarResumo());
    }
}
