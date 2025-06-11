package edu.infnet.InventorizeAPI.services;


import edu.infnet.InventorizeAPI.dto.request.AuthenticationRequestDTO;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//@Disabled
@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
    @Mock
    private JwtService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthUserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;


    @Test
    public void shouldAssertCorrectUserDataWhenRegistering() {
        var authenticationRequestDTO = mockedAuthenticationRequest();
        var mockedUser = mockedAuthUser(authenticationRequestDTO.email());
        var userCaptor = getUserCaptor();

        when(userRepository.existsByEmail(authenticationRequestDTO.email())).thenReturn(false);
        when(passwordEncoder.encode(authenticationRequestDTO.password())).thenReturn(mockedUser.getHashPassword());
        when(userRepository.save(any(AuthUser.class))).thenReturn(mockedUser);

        var userResponseDTO = authenticationService.register(authenticationRequestDTO);

        verify(userRepository).save(userCaptor.capture());

        var savedUser = userCaptor.getValue();

        assertEquals(mockedUser.getId(), userResponseDTO.id(), "O id do usuário retornado deve permanecer o mesmo");
        assertEquals(userResponseDTO.email(), savedUser.getEmail(), "O email do usuário retornado deve ser do usuário salvo");
        assertEquals(mockedUser.getHashPassword(), savedUser.getHashPassword(), "A senha criptografada do usuário salvo deve permanecer igual");
        assertEquals(mockedUser.getRoles(), savedUser.getRoles(),"As autorizações do usuário salvo devem se manter iguais");
    }

    @Test
    public void shouldCallCorrectMethodsWhenRegisteringUser() {
        var authenticationRequestDTO = mockedAuthenticationRequest();
        var mockedUser = mockedAuthUser(authenticationRequestDTO.email());

        when(userRepository.save(any(AuthUser.class))).thenReturn(mockedUser);
        when(userRepository.existsByEmail(authenticationRequestDTO.email())).thenReturn(false);
        when(passwordEncoder.encode(authenticationRequestDTO.password())).thenReturn("$2a$10$eImiTMZG4ELQ2Z8z5y3jOe");

        authenticationService.register(authenticationRequestDTO);

        verify(userRepository).existsByEmail(authenticationRequestDTO.email());
        verify(passwordEncoder).encode(authenticationRequestDTO.password());
        verify(userRepository).save(any(AuthUser.class));
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @Test
    public void shouldThrowUserAlreadyRegisteredExceptionWhenEmailExists() {
        var authenticationRequestDTO = mockedAuthenticationRequest();

        when(userRepository.existsByEmail(authenticationRequestDTO.email())).thenReturn(true);

        var userRegisteredException = assertThrows(UserAlreadyRegisteredException.class, () -> {
            authenticationService.register(authenticationRequestDTO);
        }, "Deve lançar UserAlreadyRegisteredException quando o email já estiver cadastrado");

        assertEquals(userRegisteredException.getMessage(), String.format("Email: %s já cadastrado", authenticationRequestDTO.email()));
    }

    @Test
    public void shouldAuthenticateUserAndReturnToken() {
        var authenticationRequestDTO = mockedAuthenticationRequest();
        var mockedUser = mockedAuthUser(authenticationRequestDTO.email());

        var userDetails = UserDetailsImpl.builder()
                .authUser(mockedUser)
                .build();
        var jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGVtYWlsLmNvbSJ9";
        var authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(tokenService.generateToken(any(UserDetails.class))).thenReturn(jwtToken);

        var authenticationResponseDTO = authenticationService.authenticate(authenticationRequestDTO);

        assertEquals(jwtToken, authenticationResponseDTO.token(), "O token retornado deve ser igual ao esperado");
        assertEquals(authenticationRequestDTO.email(), authenticationResponseDTO.email(), "O email retornado deve corresponder ao da requisição");
        assertEquals(mockedUser.getId(), authenticationResponseDTO.userId(), "O ID do usuário retornado deve ser igual ao esperado");
    }

    @Test
    public void shouldCallCorrectMethodsWhenAuthenticatingUser() {
        var authenticationRequestDTO = mockedAuthenticationRequest();
        var mockedUser = mockedAuthUser(authenticationRequestDTO.email());

        var userDetails = UserDetailsImpl.builder()
                .authUser(mockedUser)
                .build();
        var jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGVtYWlsLmNvbSJ9";
        var authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(tokenService.generateToken(any(UserDetails.class))).thenReturn(jwtToken);

        authenticationService.authenticate(authenticationRequestDTO);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(authentication).getPrincipal();
        verify(tokenService).generateToken(any(UserDetails.class));
        verifyNoMoreInteractions(authenticationManager, tokenService);
    }

    @Test
    public void shouldGetAuthenticatedUser() {
        var mockedAuthUser = mockedAuthUser("authenticatedUser@email.com");
        var userDetails = UserDetailsImpl.builder()
                .authUser(mockedAuthUser)
                .build();
        var authentication = mock(Authentication.class);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        var authenticatedUser = authenticationService.getAuthenticatedUser();

        assertEquals(mockedAuthUser.getId(), authenticatedUser.getId(), "O ID do usuário autenticado deve ser igual ao esperado");
    }
    @Test
    public void shouldThrowExceptionWhenUserIsNotAuthenticated() {
        var authentication = mock(Authentication.class);
        var userDetails = mock(UserDetailsImpl.class);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getAuthUser()).thenReturn(null);

        assertThrows(UserNotAuthenticatedException.class,
                () -> authenticationService.getAuthenticatedUser(),
                "Deve lançar UserNotAuthenticatedException quando nenhum usuário estiver autenticado");
    }

    @Test
    public void shouldThrowExceptionWhenUserNotFoundByEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());


        assertThrows(UsernameNotFoundException.class,
                    () -> authenticationService.findByEmail("email@email.com"),
                "Deve lançar UsernameNotFoundException quando nenhum usuário for encontrado com o email fornecido");
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