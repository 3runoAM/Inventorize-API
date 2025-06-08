package edu.infnet.InventorizeAPI.services.auth;

import edu.infnet.InventorizeAPI.entities.AuthUser;
import edu.infnet.InventorizeAPI.repository.AuthUserRepository;
import edu.infnet.InventorizeAPI.security.auth.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AuthUserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AuthUser authUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Usuário não encontrado com o [EMAIL: %s]: ", email)));

        return UserDetailsImpl.builder()
                .authUser(authUser)
                .build();
    }
}