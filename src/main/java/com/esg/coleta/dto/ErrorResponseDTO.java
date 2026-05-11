package com.esg.coleta.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponseDTO {
    private int status;
    private String erro;
    private List<String> mensagens;
    private LocalDateTime timestamp;
}
