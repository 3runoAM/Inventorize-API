package edu.infnet.InventorizeAPI.exceptions;

import edu.infnet.InventorizeAPI.exceptions.custom.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MailAuthenticationException.class)
    public ResponseEntity<String> handleMailAuthenticationException(MailAuthenticationException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("| Erro de autenticação de email: " + ex.getMessage());
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<String> handleMailException(MailException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("| Erro ao enviar email: " + ex.getMessage());
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<String> handleInsufficientStockException(InsufficientStockException ex){
        return  ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body("| Ajuste inválido: " + ex.getMessage());
    }

    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<String> handleInventoryNotFoundException(InventoryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("| Inventário não encontrado: " + ex.getMessage());
    }

    @ExceptionHandler(DeletingEntityException.class)
    public ResponseEntity<String> handleDeletingEntityException(DeletingEntityException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("| Erro ao deletar: " + e.getMessage());
    }

    @ExceptionHandler(UnauthorizedRequestException.class)
    public ResponseEntity<String> handleUnauthorizedRequestException(UnauthorizedRequestException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("| Acesso não autorizado: " + e.getMessage());
    }
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFoundException(ProductNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("| Produto não encontrado: " + e.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("| Token inválido: " + e.getMessage());
    }

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<String> handleUserAlreadyRegisteredException(UserAlreadyRegisteredException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("| Email já cadastrado: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("| Ocorreu um erro inesperado: " + e.getMessage());
    }
}