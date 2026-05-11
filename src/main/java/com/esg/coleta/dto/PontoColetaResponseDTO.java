package com.esg.coleta.dto;

import com.esg.coleta.model.TipoResiduo;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PontoColetaResponseDTO {
    private Long id;
    private String nome;
    private TipoResiduo tipoResiduo;
    private String endereco;
    private Double capacidadeKg;
    private Boolean ativo;
}
