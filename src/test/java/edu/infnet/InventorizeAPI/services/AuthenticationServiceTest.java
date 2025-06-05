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
    public void shouldRegisterUserSuccessfully() {
        AuthenticationRequestDTO userData = getAuthenticationRequest();
        var encodedPassword = "$2a$10$eImiTMZG4ELQ2Z8z5y3jOe";

        var authUser = AuthUser.builder()
                .id(UUID.fromString("6e399d0f-66ad-4092-97e7-1d755388959f"))
                .email(userData.email())
                .hashPassword(encodedPassword)
                .roles(Set.of(Role.ROLE_USER))
                .build();

        var userCaptor = ArgumentCaptor.forClass(AuthUser.class);

        when(authUserRepository.existsByEmail(userData.email())).thenReturn(false);
        when(passwordEncoder.encode(userData.password())).thenReturn(encodedPassword);
        when(authUserRepository.save(any(AuthUser.class))).thenReturn(authUser);


        UserResponseDTO serviceResponse = authenticationService.register(userData);


        verify(authUserRepository).existsByEmail(userData.email());
        verify(passwordEncoder).encode(userData.password());
        verify(authUserRepository).save(userCaptor.capture());
        verifyNoMoreInteractions(authUserRepository, passwordEncoder);

        AuthUser capturedUser = userCaptor.getValue();
        assertEquals(authUser.getId(), serviceResponse.id(), "O id do usuário registrado deve ser igual ao esperado");
        assertEquals(userData.email(), capturedUser.getEmail(), "O email do usuário registrado deve ser igual ao esperado");
        assertEquals(encodedPassword, capturedUser.getHashPassword(), "A senha criptografada do usuário registrado deve ser igual à esperada");
        assertEquals(authUser.getRoles(), capturedUser.getRoles(), "Os papéis do usuário registrado devem ser iguais aos esperados");
    }

//    @Test
//    public void shouldThrowUserAlreadyRegisteredExceptionWhenEmailExists() {
//       AuthenticationRequestDTO userData = getAuthenticationRequest();
//
//        when(authUserRepository.existsByEmail(userData.email())).thenReturn(true);
//
//        var userRegisteredException = assertThrows(UserAlreadyRegisteredException.class, () -> {
//            authenticationService.register(userData);
//        },"Deve lançar UserAlreadyRegisteredException quando o email já estiver cadastrado");
//        assertEquals(userRegisteredException.getMessage(), String.format("[ EMAIL: %s ] já cadastrado", userData.email()));
//    }

    private AuthenticationRequestDTO getAuthenticationRequest() {;
        return new AuthenticationRequestDTO("test1@email.com", "password123");
    }
}