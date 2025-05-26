package edu.infnet.InventorizeAPI.controllers;

import edu.infnet.InventorizeAPI.dto.request.AuthenticationRequestDTO;
import edu.infnet.InventorizeAPI.dto.response.AuthenticationResponseDTO;
import edu.infnet.InventorizeAPI.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(@RequestBody @Valid AuthenticationRequestDTO userData){
        AuthenticationResponseDTO authenticatedUser = authenticationService.authenticate(userData);

        return ResponseEntity.ok(authenticatedUser);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDTO> register(@RequestBody @Valid AuthenticationRequestDTO userData) {
        AuthenticationResponseDTO savedUser = authenticationService.register(userData);

        return ResponseEntity.ok(savedUser);
    }
}