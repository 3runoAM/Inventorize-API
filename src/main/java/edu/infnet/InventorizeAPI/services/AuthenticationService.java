package edu.infnet.InventorizeAPI.services;
import edu.infnet.InventorizeAPI.dto.request.AuthenticationRequestDTO;
import edu.infnet.InventorizeAPI.dto.response.AuthenticationResponseDTO;
import edu.infnet.InventorizeAPI.entities.AuthUser;
import edu.infnet.InventorizeAPI.enums.Role;
import edu.infnet.InventorizeAPI.exceptions.custom.UserNotAuthenticatedException;
import edu.infnet.InventorizeAPI.repository.AuthUserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthenticationService {
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(AuthUserRepository authUserRepository, PasswordEncoder passwordEncoder) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthUser findByEmail(String email) {
        return authUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public boolean existsByEmail(String email) {
        return authUserRepository.existsByEmail(email);
    }

    public AuthenticationResponseDTO registerUser(AuthenticationRequestDTO userData) {
        String encryptedPassword = passwordEncoder.encode(userData.password());

        Set<Role> roles = Set.of(Role.ROLE_USER);

        AuthUser authUser = AuthUser.builder()
                .email(userData.email())
                .hashPassword(encryptedPassword)
                .roles(roles)
                .build();

        AuthUser savedUser = authUserRepository.save(authUser);

        return new AuthenticationResponseDTO(null, savedUser.getEmail(), savedUser.getId());
    }

    public AuthenticationResponseDTO getAuthenticatedUserInfo() {
        AuthUser authUser = (AuthUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (authUser == null) throw new UserNotAuthenticatedException("Nenhum usuário autenticado encontrado.");

        return new AuthenticationResponseDTO(null, authUser.getEmail(), authUser.getId());
    }
}