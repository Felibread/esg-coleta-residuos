package com.esg.coleta.exception;

import com.esg.coleta.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .erro("Recurso não encontrado")
                .mensagens(List.of(ex.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> mensagens = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .erro("Dados inválidos")
                .mensagens(mensagens)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .erro("Requisição inválida")
                .mensagens(List.of(ex.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneralException(Exception ex) {
        ErrorResponseDTO error = ErrorResponseDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .erro("Erro interno do servidor")
                .mensagens(List.of(ex.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
