package edu.infnet.InventorizeAPI.services;

import edu.infnet.InventorizeAPI.dto.request.AuthenticationRequestDTO;
import edu.infnet.InventorizeAPI.dto.response.AuthenticationResponseDTO;
import edu.infnet.InventorizeAPI.entities.AuthUser;
import edu.infnet.InventorizeAPI.enums.Role;
import edu.infnet.InventorizeAPI.exceptions.custom.RegisteredEmailException;
import edu.infnet.InventorizeAPI.exceptions.custom.UserNotAuthenticatedException;
import edu.infnet.InventorizeAPI.repository.AuthUserRepository;
import edu.infnet.InventorizeAPI.services.auth.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthUserRepository authUserRepository;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponseDTO register(AuthenticationRequestDTO userData) {
        if (this.existsByEmail(userData.email())) throw new RegisteredEmailException("Email já cadastrado");

        String encryptedPassword = passwordEncoder.encode(userData.password());
        Set<Role> roles = Set.of(Role.ROLE_USER);

        var authUser = AuthUser.builder()
                .email(userData.email())
                .hashPassword(encryptedPassword)
                .roles(roles)
                .build();

        AuthUser savedUser = authUserRepository.save(authUser);

        return new AuthenticationResponseDTO(null, savedUser.getEmail(), savedUser.getId());
    }

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO userData) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(userData.email(), userData.password());
        Authentication auth = authenticationManager.authenticate(usernamePassword);

        String token = tokenService.generateToken((UserDetails) auth.getPrincipal());
        AuthUser user = this.findByEmail(userData.email());

        return new AuthenticationResponseDTO(token, user.getEmail(), user.getId());
    }

    /*Métodos utilitários*/
    public AuthenticationResponseDTO getAuthenticatedUserInfo() {
        AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (authUser == null) throw new UserNotAuthenticatedException("Nenhum usuário autenticado encontrado.");

        return new AuthenticationResponseDTO(null, authUser.getEmail(), authUser.getId());
    }

    public AuthUser findByEmail(String email) {
        return authUserRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public boolean existsByEmail(String email) {
        return authUserRepository.existsByEmail(email);
    }
}