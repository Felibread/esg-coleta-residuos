package com.esg.coleta.controller;

import com.esg.coleta.dto.PontoColetaRequestDTO;
import com.esg.coleta.dto.PontoColetaResponseDTO;
import com.esg.coleta.service.PontoColetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pontos-coleta")
@RequiredArgsConstructor
public class PontoColetaController {

    private final PontoColetaService service;

    @PostMapping
    public ResponseEntity<PontoColetaResponseDTO> criar(
            @Valid @RequestBody PontoColetaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<PontoColetaResponseDTO>> listarTodos(
            @RequestParam(required = false) Boolean apenasAtivos) {
        if (Boolean.TRUE.equals(apenasAtivos)) {
            return ResponseEntity.ok(service.listarAtivos());
        }
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PontoColetaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PontoColetaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PontoColetaRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        service.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
