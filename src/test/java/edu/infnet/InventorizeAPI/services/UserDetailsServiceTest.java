package edu.infnet.InventorizeAPI.services;

import edu.infnet.InventorizeAPI.entities.AuthUser;
import edu.infnet.InventorizeAPI.repository.AuthUserRepository;
import edu.infnet.InventorizeAPI.security.auth.UserDetailsImpl;
import edu.infnet.InventorizeAPI.services.auth.UserDetailsServiceImpl;
import edu.infnet.InventorizeAPI.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceTest {
    @Mock
    private AuthUserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    public void shouldLoadUserByEmailCorrectly() {
        var user = AuthUser.builder()
                .id(UUID.fromString("96f1e8b9-1647-4050-a45f-5ea67fcf752d"))
                .email("teste@email.com")
                .hashPassword("e98a6c0f43b26d7d80b5c28b9765221f")
                .roles(Set.of(Role.ROLE_USER))
                .build();
        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        var userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(user.getEmail());

        assertEquals(authorities, userDetails.getAuthorities(), "As autoridades do usuário devem estar corretas");
        assertEquals(user.getEmail(), userDetails.getUsername(), "O usuário carregado deve ter o email correto");
        assertEquals(user.getHashPassword(), userDetails.getPassword(), "A senha hasheada do usuário carregado deve estar correta");
        assertTrue(userDetails.isAccountNonLocked(), "A conta do usuário deve estar desbloqueada");
        assertTrue(userDetails.isCredentialsNonExpired(), "As credenciais do usuário devem estar válidas");
        assertTrue(userDetails.isEnabled(), "O usuário deve estar habilitado");
    }

    @Test
    public void shouldThrowExceptionWhenUserNotFound() {
        var user = AuthUser.builder()
                .id(UUID.fromString("96f1e8b9-1647-4050-a45f-5ea67fcf752d"))
                .email("teste@email.com")
                .hashPassword("e98a6c0f43b26d7d80b5c28b9765221f")
                .roles(Set.of(Role.ROLE_USER))
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        var usernameNotFoundException = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(user.getEmail()),
                "Deve lançar UsernameNotFoundException quando o usuário não for encontrado pelo email");

        assertEquals(String.format("Usuário não encontrado com o [EMAIL: %s]: ", user.getEmail()), usernameNotFoundException.getMessage());
    }
}
