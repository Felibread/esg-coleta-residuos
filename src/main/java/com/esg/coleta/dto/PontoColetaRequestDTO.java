package com.esg.coleta.dto;

import com.esg.coleta.model.TipoResiduo;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PontoColetaRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @NotNull(message = "Tipo de resíduo é obrigatório")
    private TipoResiduo tipoResiduo;

    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;

    @NotNull(message = "Capacidade é obrigatória")
    @Positive(message = "Capacidade deve ser um valor positivo")
    private Double capacidadeKg;
}
