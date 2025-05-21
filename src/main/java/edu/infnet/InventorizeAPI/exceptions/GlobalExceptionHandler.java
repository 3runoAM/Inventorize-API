package edu.infnet.InventorizeAPI.exceptions;

import edu.infnet.InventorizeAPI.exceptions.custom.InvalidTokenException;
import edu.infnet.InventorizeAPI.exceptions.custom.RegisteredEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("| Token inválido: " + e.getMessage());
    }

    @ExceptionHandler(RegisteredEmailException.class)
    public ResponseEntity<String> handleRegisteredEmailException(RegisteredEmailException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("| Email já cadastrado: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("| Ocorreu um erro inesperado: " + e.getMessage());
    }
}