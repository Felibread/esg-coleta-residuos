package com.esg.coleta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "coletas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Ponto de coleta é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ponto_coleta_id", nullable = false)
    private PontoColeta pontoColeta;

    @NotNull(message = "Data da coleta é obrigatória")
    @Column(nullable = false)
    private LocalDate dataColeta;

    @NotNull(message = "Peso coletado é obrigatório")
    @Positive(message = "Peso deve ser positivo")
    @Column(nullable = false)
    private Double pesoColetadoKg;

    @NotBlank(message = "Responsável é obrigatório")
    @Column(nullable = false)
    private String responsavel;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private StatusColeta status = StatusColeta.AGENDADA;

    @Column(length = 500)
    private String observacoes;
}
