package com.esg.coleta.service;

import com.esg.coleta.dto.ColetaRequestDTO;
import com.esg.coleta.dto.ColetaResponseDTO;
import com.esg.coleta.exception.RecursoNaoEncontradoException;
import com.esg.coleta.model.Coleta;
import com.esg.coleta.model.PontoColeta;
import com.esg.coleta.model.StatusColeta;
import com.esg.coleta.repository.ColetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ColetaService {

    private final ColetaRepository coletaRepository;
    private final PontoColetaService pontoColetaService;

    @Transactional
    public ColetaResponseDTO registrar(ColetaRequestDTO dto) {
        PontoColeta ponto = pontoColetaService.buscarEntidade(dto.getPontoColetaId());

        if (!ponto.getAtivo()) {
            throw new IllegalArgumentException(
                    "Ponto de coleta inativo. Não é possível registrar coleta.");
        }

        Coleta coleta = Coleta.builder()
                .pontoColeta(ponto)
                .dataColeta(dto.getDataColeta())
                .pesoColetadoKg(dto.getPesoColetadoKg())
                .responsavel(dto.getResponsavel())
                .status(dto.getStatus() != null ? dto.getStatus() : StatusColeta.AGENDADA)
                .observacoes(dto.getObservacoes())
                .build();

        return toDTO(coletaRepository.save(coleta));
    }

    @Transactional(readOnly = true)
    public List<ColetaResponseDTO> listarTodas() {
        return coletaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ColetaResponseDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<ColetaResponseDTO> buscarPorPonto(Long pontoId) {
        return coletaRepository.findByPontoColetaId(pontoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ColetaResponseDTO atualizarStatus(Long id, StatusColeta novoStatus) {
        Coleta coleta = buscarEntidade(id);
        coleta.setStatus(novoStatus);
        return toDTO(coletaRepository.save(coleta));
    }

    @Transactional
    public void deletar(Long id) {
        buscarEntidade(id);
        coletaRepository.deleteById(id);
    }

    private Coleta buscarEntidade(Long id) {
        return coletaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Coleta não encontrada com ID: " + id));
    }

    private ColetaResponseDTO toDTO(Coleta c) {
        return ColetaResponseDTO.builder()
                .id(c.getId())
                .pontoColetaId(c.getPontoColeta().getId())
                .nomePontoColeta(c.getPontoColeta().getNome())
                .tipoResiduo(c.getPontoColeta().getTipoResiduo())
                .dataColeta(c.getDataColeta())
                .pesoColetadoKg(c.getPesoColetadoKg())
                .responsavel(c.getResponsavel())
                .status(c.getStatus())
                .observacoes(c.getObservacoes())
                .build();
    }
}
