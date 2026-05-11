package com.esg.coleta.dto;

import com.esg.coleta.model.StatusColeta;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColetaRequestDTO {

    @NotNull(message = "ID do ponto de coleta é obrigatório")
    private Long pontoColetaId;

    @NotNull(message = "Data da coleta é obrigatória")
    private LocalDate dataColeta;

    @NotNull(message = "Peso coletado é obrigatório")
    @Positive(message = "Peso deve ser um valor positivo")
    private Double pesoColetadoKg;

    @NotBlank(message = "Responsável é obrigatório")
    private String responsavel;

    private StatusColeta status;

    private String observacoes;
}
