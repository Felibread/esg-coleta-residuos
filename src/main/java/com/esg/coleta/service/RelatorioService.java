package com.esg.coleta.service;

import com.esg.coleta.dto.RelatorioESGDTO;
import com.esg.coleta.model.StatusColeta;
import com.esg.coleta.repository.ColetaRepository;
import com.esg.coleta.repository.PontoColetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final PontoColetaRepository pontoColetaRepository;
    private final ColetaRepository coletaRepository;

    // Fator de CO2 evitado por kg de resíduo reciclado (estimativa média)
    private static final double FATOR_CO2_KG = 2.5;

    @Transactional(readOnly = true)
    public RelatorioESGDTO gerarResumo() {
        long totalPontosAtivos = pontoColetaRepository.countByAtivo(true);
        long totalColetas      = coletaRepository.count();
        long concluidas        = coletaRepository.countByStatus(StatusColeta.CONCLUIDA);
        long pendentes         = coletaRepository.countByStatus(StatusColeta.AGENDADA)
                               + coletaRepository.countByStatus(StatusColeta.EM_ANDAMENTO);

        Double totalPeso = coletaRepository.sumPesoColetadoKgConcluidas();
        if (totalPeso == null) totalPeso = 0.0;

        Double mediaPeso = coletaRepository.avgPesoColetadoKg().orElse(0.0);
        double co2Evitado = totalPeso * FATOR_CO2_KG;

        String impacto = gerarDescricaoImpacto(totalPeso, co2Evitado, concluidas);

        return RelatorioESGDTO.builder()
                .totalPontosAtivos(totalPontosAtivos)
                .totalColetasRealizadas(totalColetas)
                .totalPesoColetadoKg(totalPeso)
                .mediaPesoColetadoKg(Math.round(mediaPeso * 100.0) / 100.0)
                .coletasConcluidas(concluidas)
                .coletasPendentes(pendentes)
                .impactoAmbiental(impacto)
                .co2EvitadoKg(Math.round(co2Evitado * 100.0) / 100.0)
                .build();
    }

    private String gerarDescricaoImpacto(double totalPeso, double co2Evitado, long concluidas) {
        if (concluidas == 0) {
            return "Nenhuma coleta concluída ainda. Comece a registrar coletas para acompanhar o impacto ESG.";
        }
        return String.format(
                "Através de %d coleta(s) concluída(s), foram reciclados %.2f kg de resíduos, " +
                "evitando a emissão de aproximadamente %.2f kg de CO2 equivalente. " +
                "Contribuição positiva para os Objetivos de Desenvolvimento Sustentável (ODS).",
                concluidas, totalPeso, co2Evitado);
    }
}
