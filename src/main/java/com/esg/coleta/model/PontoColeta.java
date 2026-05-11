package com.esg.coleta.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "pontos_coleta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PontoColeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Column(nullable = false)
    private String nome;

    @NotNull(message = "Tipo de resíduo é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoResiduo tipoResiduo;

    @NotBlank(message = "Endereço é obrigatório")
    @Column(nullable = false)
    private String endereco;

    @NotNull(message = "Capacidade é obrigatória")
    @Positive(message = "Capacidade deve ser positiva")
    @Column(nullable = false)
    private Double capacidadeKg;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;
}
