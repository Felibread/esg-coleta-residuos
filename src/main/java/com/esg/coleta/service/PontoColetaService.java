package com.esg.coleta.service;

import com.esg.coleta.dto.PontoColetaRequestDTO;
import com.esg.coleta.dto.PontoColetaResponseDTO;
import com.esg.coleta.exception.RecursoNaoEncontradoException;
import com.esg.coleta.model.PontoColeta;
import com.esg.coleta.repository.PontoColetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PontoColetaService {

    private final PontoColetaRepository repository;

    @Transactional
    public PontoColetaResponseDTO criar(PontoColetaRequestDTO dto) {
        PontoColeta ponto = PontoColeta.builder()
                .nome(dto.getNome())
                .tipoResiduo(dto.getTipoResiduo())
                .endereco(dto.getEndereco())
                .capacidadeKg(dto.getCapacidadeKg())
                .ativo(true)
                .build();
        return toDTO(repository.save(ponto));
    }

    @Transactional(readOnly = true)
    public List<PontoColetaResponseDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PontoColetaResponseDTO> listarAtivos() {
        return repository.findByAtivo(true).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PontoColetaResponseDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    @Transactional
    public PontoColetaResponseDTO atualizar(Long id, PontoColetaRequestDTO dto) {
        PontoColeta ponto = buscarEntidade(id);
        ponto.setNome(dto.getNome());
        ponto.setTipoResiduo(dto.getTipoResiduo());
        ponto.setEndereco(dto.getEndereco());
        ponto.setCapacidadeKg(dto.getCapacidadeKg());
        return toDTO(repository.save(ponto));
    }

    @Transactional
    public void desativar(Long id) {
        PontoColeta ponto = buscarEntidade(id);
        ponto.setAtivo(false);
        repository.save(ponto);
    }

    @Transactional
    public void deletar(Long id) {
        buscarEntidade(id);
        repository.deleteById(id);
    }

    public PontoColeta buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Ponto de coleta não encontrado com ID: " + id));
    }

    private PontoColetaResponseDTO toDTO(PontoColeta p) {
        return PontoColetaResponseDTO.builder()
                .id(p.getId())
                .nome(p.getNome())
                .tipoResiduo(p.getTipoResiduo())
                .endereco(p.getEndereco())
                .capacidadeKg(p.getCapacidadeKg())
                .ativo(p.getAtivo())
                .build();
    }
}
