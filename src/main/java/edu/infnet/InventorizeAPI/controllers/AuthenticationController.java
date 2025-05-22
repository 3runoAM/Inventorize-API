package edu.infnet.InventorizeAPI.controllers;

import edu.infnet.InventorizeAPI.dto.request.AuthenticationRequestDTO;
import edu.infnet.InventorizeAPI.dto.response.AuthenticationResponseDTO;
import edu.infnet.InventorizeAPI.entities.AuthUser;
import edu.infnet.InventorizeAPI.exceptions.custom.RegisteredEmailException;
import edu.infnet.InventorizeAPI.services.AuthenticationService;
import edu.infnet.InventorizeAPI.services.auth.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("auth")
public class AuthenticationController {
    private final JwtService tokenService;
    private final AuthenticationManager authenticationManager;
    private final AuthenticationService userRegistrationService;

    public AuthenticationController(JwtService tokenService, AuthenticationManager authenticationManager, AuthenticationService userRegistrationService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRegistrationService = userRegistrationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(@RequestBody @Valid AuthenticationRequestDTO userData){
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(userData.email(), userData.password());
        Authentication auth = authenticationManager.authenticate(usernamePassword);

        String token = tokenService.generateToken((UserDetails) auth.getPrincipal());
        AuthUser user = userRegistrationService.findByEmail(userData.email());

        return ResponseEntity.ok(AuthenticationResponseDTO.from(token, user));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDTO> register(@RequestBody @Valid AuthenticationRequestDTO userData) {
        if (userRegistrationService.existsByEmail(userData.email())) throw new RegisteredEmailException("Email já cadastrado");

        AuthenticationResponseDTO savedUser = userRegistrationService.registerUser(userData);

        return ResponseEntity.ok(savedUser);
    }
}