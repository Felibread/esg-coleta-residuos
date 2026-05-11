package com.esg.coleta.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelatorioESGDTO {
    private Long totalPontosAtivos;
    private Long totalColetasRealizadas;
    private Double totalPesoColetadoKg;
    private Double mediaPesoColetadoKg;
    private Long coletasConcluidas;
    private Long coletasPendentes;
    private String impactoAmbiental;
    private Double co2EvitadoKg; // CO2 evitado estimado
}
