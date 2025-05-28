package edu.infnet.InventorizeAPI.controllers;

import edu.infnet.InventorizeAPI.dto.request.AuthenticationRequestDTO;
import edu.infnet.InventorizeAPI.dto.response.AuthenticationResponseDTO;
import edu.infnet.InventorizeAPI.dto.response.UserResponseDTO;
import edu.infnet.InventorizeAPI.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    /**
     * Endpoint para autenticar um usuário.
     *
     * @param userData Dados de autenticação do usuário.
     * @return Resposta com os dados do usuário autenticado.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(@RequestBody @Valid AuthenticationRequestDTO userData){
        AuthenticationResponseDTO authenticatedUser = authenticationService.authenticate(userData);

        return ResponseEntity.ok(authenticatedUser);
    }

    /**
     * Endpoint para registrar um novo usuário.
     *
     * @param userData Dados do usuário a ser registrado.
     * @return Resposta com os dados do usuário registrado.
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid AuthenticationRequestDTO userData) {
        UserResponseDTO savedUser = authenticationService.register(userData);

        return ResponseEntity.ok(savedUser);
    }
}