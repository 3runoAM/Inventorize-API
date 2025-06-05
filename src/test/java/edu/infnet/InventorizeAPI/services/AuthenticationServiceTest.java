package edu.infnet.InventorizeAPI.services;


import edu.infnet.InventorizeAPI.dto.request.AuthenticationRequestDTO;
import edu.infnet.InventorizeAPI.dto.response.UserResponseDTO;
import edu.infnet.InventorizeAPI.entities.AuthUser;
import edu.infnet.InventorizeAPI.enums.Role;
import edu.infnet.InventorizeAPI.exceptions.custom.UserAlreadyRegisteredException;
import edu.infnet.InventorizeAPI.exceptions.custom.UserNotAuthenticatedException;
import edu.infnet.InventorizeAPI.repository.AuthUserRepository;
import edu.infnet.InventorizeAPI.security.auth.UserDetailsImpl;
import edu.infnet.InventorizeAPI.services.auth.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    @Mock
    private JwtService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;


    @Test
    public void shouldAssertCorrectUserDataWhenRegistering() {
        var authenticationRequestDTO = mockedAuthenticationRequest();
        var mockedSavedUser = mockedAuthUser(authenticationRequestDTO.email());
        var userCaptor = getUserCaptor();

        when(authUserRepository.existsByEmail(authenticationRequestDTO.email())).thenReturn(false);
        when(passwordEncoder.encode(authenticationRequestDTO.password())).thenReturn(mockedSavedUser.getHashPassword());
        when(authUserRepository.save(any(AuthUser.class))).thenReturn(mockedSavedUser);

        UserResponseDTO userResponseDTO = authenticationService.register(authenticationRequestDTO);

        verify(authUserRepository).save(userCaptor.capture());

        var capturedUser = userCaptor.getValue();

        assertEquals(mockedSavedUser.getId(), userResponseDTO.id(), "O id do usuário registrado deve ser igual ao esperado");
        assertEquals(capturedUser.getEmail(), userResponseDTO.email(), "O email do usuário registrado deve ser igual ao esperado");
        assertEquals(mockedSavedUser.getHashPassword(), capturedUser.getHashPassword(), "A senha criptografada do usuário registrado deve ser igual à esperada");
        assertEquals(mockedSavedUser.getRoles(), capturedUser.getRoles(), "Os papéis do usuário registrado devem ser iguais aos esperados");
    }

    @Test
    public void shouldCallCorrectMethodsWhenRegisteringUser() {
        var authenticationRequestDTO = mockedAuthenticationRequest();
        var mockedSavedUser = mockedAuthUser(authenticationRequestDTO.email());

        when(authUserRepository.save(any(AuthUser.class))).thenReturn(mockedSavedUser);
        when(authUserRepository.existsByEmail(authenticationRequestDTO.email())).thenReturn(false);
        when(passwordEncoder.encode(authenticationRequestDTO.password())).thenReturn("$2a$10$eImiTMZG4ELQ2Z8z5y3jOe");

        authenticationService.register(authenticationRequestDTO);

        verify(authUserRepository).existsByEmail(authenticationRequestDTO.email());
        verify(passwordEncoder).encode(authenticationRequestDTO.password());
        verify(authUserRepository).save(any(AuthUser.class));
        verifyNoMoreInteractions(authUserRepository, passwordEncoder);
    }

    @Test
    public void shouldThrowUserAlreadyRegisteredExceptionWhenEmailExists() {
        var authenticationRequestDTO = mockedAuthenticationRequest();

        when(authUserRepository.existsByEmail(authenticationRequestDTO.email())).thenReturn(true);

        var userRegisteredException = assertThrows(UserAlreadyRegisteredException.class, () -> {
            authenticationService.register(authenticationRequestDTO);
        }, "Deve lançar UserAlreadyRegisteredException quando o email já estiver cadastrado");

        assertEquals(userRegisteredException.getMessage(), String.format("[ EMAIL: %s ] já cadastrado", authenticationRequestDTO.email()));
    }

    @Test
    public void shouldAuthenticateUserAndReturnToken() {
        var authenticationRequestDTO = mockedAuthenticationRequest();
        var mockedSavedUser = mockedAuthUser(authenticationRequestDTO.email());

        var userDetails = UserDetailsImpl.builder()
                .authUser(mockedSavedUser)
                .build();
        var jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGVtYWlsLmNvbSJ9";
        var authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(tokenService.generateToken(any(UserDetails.class))).thenReturn(jwtToken);
        when(authUserRepository.findByEmail(authenticationRequestDTO.email())).thenReturn(Optional.of(mockedSavedUser));

        var authenticationResponseDTO = authenticationService.authenticate(authenticationRequestDTO);

        assertEquals(jwtToken, authenticationResponseDTO.token(), "O token retornado deve ser igual ao esperado");
        assertEquals(authenticationRequestDTO.email(), authenticationResponseDTO.email(), "O email retornado deve corresponder ao da requisição");
        assertEquals(mockedSavedUser.getId(), authenticationResponseDTO.userId(), "O ID do usuário retornado deve ser igual ao esperado");
    }

    @Test
    public void shouldCallCorrectMethodsWhenAuthenticatingUser() {
        var authenticationRequestDTO = mockedAuthenticationRequest();
        var mockedSavedUser = mockedAuthUser(authenticationRequestDTO.email());

        var userDetails = UserDetailsImpl.builder()
                .authUser(mockedSavedUser)
                .build();
        var jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGVtYWlsLmNvbSJ9";
        var authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(tokenService.generateToken(any(UserDetails.class))).thenReturn(jwtToken);
        when(authUserRepository.findByEmail(authenticationRequestDTO.email())).thenReturn(Optional.of(mockedSavedUser));

        authenticationService.authenticate(authenticationRequestDTO);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authentication).getPrincipal();
        verify(tokenService).generateToken(any(UserDetails.class));
        verify(authUserRepository).findByEmail(authenticationRequestDTO.email());
        verifyNoMoreInteractions(authenticationManager, tokenService, authUserRepository);
    }

    @Test
    public void shouldGetAuthenticatedUser() {
        var mockedAuthUser = mockedAuthUser("authenticatedUser@email.com");
        var authentication = mock(Authentication.class);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(mockedAuthUser);

        AuthUser authenticatedUser = authenticationService.getAuthenticatedUser();

        assertEquals(mockedAuthUser.getId(), authenticatedUser.getId(), "O ID do usuário autenticado deve ser igual ao esperado");
    }

    @Test
    public void shouldThrowExceptionWhenNoUserIsAuthenticated() {
        var authentication = mock(Authentication.class);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(null);

        assertThrows(UserNotAuthenticatedException.class, () -> {
            authenticationService.getAuthenticatedUser();
        });
    }

    // Métodos auxiliares -----------------------

    private AuthenticationRequestDTO mockedAuthenticationRequest() {
        return new AuthenticationRequestDTO("test@email.com", "password123");
    }

    private AuthUser mockedAuthUser(String email) {
        return AuthUser.builder()
                .id(UUID.fromString("6e399d0f-66ad-4092-97e7-1d755388959f"))
                .email(email)
                .hashPassword("$2a$10$eImiTMZG4ELQ2Z8z5y3jOe")
                .roles(Set.of(Role.ROLE_USER))
                .build();
    }

    private ArgumentCaptor<AuthUser> getUserCaptor() {
        return ArgumentCaptor.forClass(AuthUser.class);
    }
}