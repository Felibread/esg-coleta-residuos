package com.esg.coleta.dto;

import com.esg.coleta.model.StatusColeta;
import com.esg.coleta.model.TipoResiduo;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColetaResponseDTO {
    private Long id;
    private Long pontoColetaId;
    private String nomePontoColeta;
    private TipoResiduo tipoResiduo;
    private LocalDate dataColeta;
    private Double pesoColetadoKg;
    private String responsavel;
    private StatusColeta status;
    private String observacoes;
}
