package edu.infnet.inventorize.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        int status,
        String message,
        Object errorDetails,
        LocalDateTime timestamp
) {
    public static ErrorResponse from(int status, String mensagem, Object detalhes) {
        return new ErrorResponse(
                status,
                mensagem,
                detalhes,
                LocalDateTime.now()
        );
    }
}