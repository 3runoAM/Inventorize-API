package edu.infnet.InventorizeAPI.exceptions;


import edu.infnet.InventorizeAPI.exceptions.custom.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> handleSignatureException(SignatureException ex) {
        var erro = ErrorResponse.from(
                HttpStatus.UNAUTHORIZED.value(),
                "Token inválido ou assinatura incorreta",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errorDetails = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .toList();

        var erro = ErrorResponse.from(HttpStatus.BAD_REQUEST.value(), "Erro de validação", errorDetails);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJwtException(MalformedJwtException ex) {
        var erro = ErrorResponse.from(
                HttpStatus.UNAUTHORIZED.value(),
                "Token mal formatado",
                "O token fornecido está em um formato inválido");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException ex) {
        var erro = ErrorResponse.from(
                HttpStatus.UNAUTHORIZED.value(),
                "Sua sessão expirou. Por favor, faça login novamente.",
                "JWT expirou em " + ex.getClaims().getExpiration());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        var erro = ErrorResponse.from(
                HttpStatus.UNAUTHORIZED.value(),
                "Erro de autenticação",
                ex.getMessage());


        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(MailAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleMailAuthenticationException(MailAuthenticationException ex){
        var erro = ErrorResponse.from(
                HttpStatus.UNAUTHORIZED.value(),
                "Erro de autenticação de email",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<ErrorResponse> handleMailException(MailException ex){
        var erro = ErrorResponse.from(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro ao enviar email",
                ex.getMessage());


        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStockException(InsufficientStockException ex){
        var erro = ErrorResponse.from(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Ajuste inválido",
                ex.getMessage());

        return  ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);
    }

    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleInventoryNotFoundException(InventoryNotFoundException ex) {
        var erro = ErrorResponse.from(
                HttpStatus.NOT_FOUND.value(),
                "Inventário não encontrado",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(DeletingEntityException.class)
    public ResponseEntity<ErrorResponse> handleDeletingEntityException(DeletingEntityException ex) {
        var erro = ErrorResponse.from(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro ao deletar",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }

    @ExceptionHandler(UnauthorizedRequestException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedRequestException(UnauthorizedRequestException ex) {
        var erro = ErrorResponse.from(
                HttpStatus.UNAUTHORIZED.value(),
                "Acesso não autorizado",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException ex) {
        var erro = ErrorResponse.from(
                HttpStatus.NOT_FOUND.value(),
                "Produto não encontrado",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException ex) {
        var erro = ErrorResponse.from(
                HttpStatus.UNAUTHORIZED.value(),
                "Token inválido",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyRegisteredException(UserAlreadyRegisteredException ex) {
        var erro = ErrorResponse.from(
                HttpStatus.CONFLICT.value(),
                "Email já cadastrado",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        var erro = ErrorResponse.from(
                HttpStatus.NOT_FOUND.value(),
                "Usuário não encontrado",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        var erro = ErrorResponse.from(
                HttpStatus.UNAUTHORIZED.value(),
                "Credenciais inválidas",
                List.of(ex.getMessage())
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        var erro = ErrorResponse.from(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um erro inesperado",
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}