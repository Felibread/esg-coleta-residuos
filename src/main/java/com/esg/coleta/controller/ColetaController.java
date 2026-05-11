package com.esg.coleta.controller;

import com.esg.coleta.dto.ColetaRequestDTO;
import com.esg.coleta.dto.ColetaResponseDTO;
import com.esg.coleta.model.StatusColeta;
import com.esg.coleta.service.ColetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coletas")
@RequiredArgsConstructor
public class ColetaController {

    private final ColetaService service;

    @PostMapping
    public ResponseEntity<ColetaResponseDTO> registrar(
            @Valid @RequestBody ColetaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<ColetaResponseDTO>> listarTodas(
            @RequestParam(required = false) Long pontoColetaId) {
        if (pontoColetaId != null) {
            return ResponseEntity.ok(service.buscarPorPonto(pontoColetaId));
        }
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColetaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ColetaResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusColeta status) {
        return ResponseEntity.ok(service.atualizarStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
