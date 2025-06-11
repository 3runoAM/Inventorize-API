package edu.infnet.inventorize.security.auth;

import edu.infnet.inventorize.entities.AuthUser;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;


@Builder
public class UserDetailsImpl implements UserDetails {
    private final AuthUser authUser;

    /**
     * Retorna as autoridades (permissões) concedidas ao usuário.
     *
     * @return uma coleção de GrantedAuthority que representam as permissões do usuário.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toSet());
    }

    /**
     * Retorna a senha do usuário.
     *
     * @return a senha hash do usuário
     */
    @Override
    public String getPassword() {
        return authUser.getHashPassword();
    }

    /**
     * Retorna o username, no caso o email do usuário.
     *
     * @return o username do usuário
     */
    @Override
    public String getUsername() {
        return authUser.getEmail();
    }

    public AuthUser getAuthUser() {
        return this.authUser;
    }

    /**
            * Indica se a conta do usuário não está bloqueada.
            *
            * @return true se a conta do usuário não estiver bloqueada
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica se as credenciais do usuário (senha) não estão expiradas.
     *
     * @return true se as credenciais do usuário não estiverem expiradas
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica se o usuário está habilitado.
     *
     * @return true se o usuário estiver habilitado
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}