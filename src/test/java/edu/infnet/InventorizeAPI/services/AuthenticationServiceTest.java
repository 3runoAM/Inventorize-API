package edu.infnet.InventorizeAPI.services;


import edu.infnet.InventorizeAPI.dto.request.AuthenticationRequestDTO;
import edu.infnet.InventorizeAPI.dto.response.UserResponseDTO;
import edu.infnet.InventorizeAPI.entities.AuthUser;
import edu.infnet.InventorizeAPI.enums.Role;
import edu.infnet.InventorizeAPI.exceptions.custom.UserAlreadyRegisteredException;
import edu.infnet.InventorizeAPI.repository.AuthUserRepository;
import edu.infnet.InventorizeAPI.services.auth.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

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
        var userData = mockedAuthenticationRequest();

        when(authUserRepository.existsByEmail(userData.email())).thenReturn(true);

        var userRegisteredException = assertThrows(UserAlreadyRegisteredException.class, () -> {
            authenticationService.register(userData);
        }, "Deve lançar UserAlreadyRegisteredException quando o email já estiver cadastrado");

        assertEquals(userRegisteredException.getMessage(), String.format("[ EMAIL: %s ] já cadastrado", userData.email()));
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