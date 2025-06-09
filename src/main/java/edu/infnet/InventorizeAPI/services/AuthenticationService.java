package edu.infnet.InventorizeAPI.services;

import edu.infnet.InventorizeAPI.dto.request.AuthenticationRequestDTO;
import edu.infnet.InventorizeAPI.dto.response.AuthenticationResponseDTO;
import edu.infnet.InventorizeAPI.dto.response.UserResponseDTO;
import edu.infnet.InventorizeAPI.entities.AuthUser;
import edu.infnet.InventorizeAPI.enums.Role;
import edu.infnet.InventorizeAPI.exceptions.custom.UserAlreadyRegisteredException;
import edu.infnet.InventorizeAPI.exceptions.custom.UserNotAuthenticatedException;
import edu.infnet.InventorizeAPI.repository.AuthUserRepository;
import edu.infnet.InventorizeAPI.security.auth.UserDetailsImpl;
import edu.infnet.InventorizeAPI.services.auth.JwtService;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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

    /**
     * Registra um novo usuário no sistema.
     *
     * @param userData Dados do usuário a ser registrado.
     * @return Informações do usuário registrado.
     */
    public UserResponseDTO register(AuthenticationRequestDTO userData) {
        if (this.existsByEmail(userData.email())) throw new UserAlreadyRegisteredException(String.format("[ EMAIL: %s ] já cadastrado", userData.email()));

        String encryptedPassword = passwordEncoder.encode(userData.password());

        var authUser = AuthUser.builder()
                .email(userData.email())
                .hashPassword(encryptedPassword)
                .roles(Set.of(Role.ROLE_USER))
                .build();

        AuthUser savedUser = authUserRepository.save(authUser);

        return UserResponseDTO.from(savedUser);
    }

    /**
     * Autentica um usuário com base nos dados fornecidos.
     *
     * @param userData Dados de autenticação do usuário.
     * @return Resposta com o token de autenticação e informações do usuário.
     */
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO userData) {
        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(userData.email(), userData.password());
        Authentication auth = authenticationManager.authenticate(usernamePassword);

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        String token = tokenService.generateToken(userDetails);
        AuthUser user = userDetails.getAuthUser();

        return AuthenticationResponseDTO.from(token, user);
    }

    /**
     * Obtém o usuário autenticado atualmente.
     *
     * @return O usuário autenticado.
     * @throws UserNotAuthenticatedException Se nenhum usuário estiver autenticado.
     */
    protected AuthUser getAuthenticatedUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        AuthUser authUser = userDetails.getAuthUser();
        if (authUser == null) throw new UserNotAuthenticatedException("Nenhum usuário autenticado encontrado.");

        return authUser;
    }

    /**
     * Busca um usuário pelo email.
     *
     * @param email Email do usuário a ser buscado.
     * @return O usuário encontrado.
     * @throws UsernameNotFoundException Se nenhum usuário for encontrado com o email fornecido.
     */
    protected AuthUser findByEmail(String email) {
        return authUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));
    }

    /**
     * Verifica se um usuário existe pelo email.
     *
     * @param email Email do usuário a ser verificado.
     * @return true se o usuário existir, false caso contrário.
     */
    protected boolean existsByEmail(String email) {
        return authUserRepository.existsByEmail(email);
    }
}