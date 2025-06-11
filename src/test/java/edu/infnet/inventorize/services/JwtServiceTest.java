package edu.infnet.inventorize.services;

import edu.infnet.inventorize.entities.AuthUser;
import edu.infnet.inventorize.enums.Role;
import edu.infnet.inventorize.security.auth.UserDetailsImpl;
import edu.infnet.inventorize.services.auth.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {
    private JwtService jwtService;

    private final String SECRET = "cf7029e1a83d5b47f2e8c0a6b1d34f89e25a8b3c7d6e40921f05c3b18a9e27d0b1a4f6c59e328d7b04c2a3f15e6d78a9";

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
    }

    @Test
    public void shouldGenerateValidTokenForUser() {
        var user = AuthUser.builder()
                .id(UUID.fromString("e53f5539-a3d9-4b12-b2b5-50f4bfd40fa5"))
                .email("teste@email.com")
                .hashPassword("$2a$10$eImiTMZG4T8YQ1zW9f5O3u5b1Z5F6k5d5f5e5f5e5f5e5f5e5f5e")
                .roles(Set.of(Role.ROLE_USER))
                .build();

        UserDetails userDetails = UserDetailsImpl
                .builder()
                .authUser(user)
                .build();


        var token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertEquals(user.getEmail(), jwtService.getUsername(token));
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    public void shouldGenerateDifferentTokensForDifferentUsers() {
        var userA = AuthUser.builder()
                .id(UUID.fromString("e53f5539-a3d9-4b12-b2b5-50f4bfd40fa5"))
                .email("teste@email.com")
                .hashPassword("b918f5d7e8350a33ed7f912591c1225e")
                .roles(Set.of(Role.ROLE_USER))
                .build();

        var userB = AuthUser.builder()
                .id(UUID.fromString("8e8bfd75-854f-46f8-a443-ba20beaa73de"))
                .email("email@teste.com")
                .hashPassword("3b8f18ccb6ad1d1b79706f205f744b38")
                .roles(Set.of(Role.ROLE_USER))
                .build();

        UserDetails userDetailsA = UserDetailsImpl
                .builder()
                .authUser(userA)
                .build();

        UserDetails userDetailsB = UserDetailsImpl
                .builder()
                .authUser(userB)
                .build();

        var tokenA = jwtService.generateToken(userDetailsA);
        var tokenB = jwtService.generateToken(userDetailsB);

        assertFalse(jwtService.isTokenValid(tokenB, userDetailsA));
        assertFalse(jwtService.isTokenValid(tokenA, userDetailsB));
    }

    @Test
    public void shouldThrownExceptionWhenExpiredJwtException() {
        var user = AuthUser.builder()
                .id(UUID.fromString("e53f5539-a3d9-4b12-b2b5-50f4bfd40fa5"))
                .email("teste@email.com")
                .hashPassword("b918f5d7e8350a33ed7f912591c1225e")
                .roles(Set.of(Role.ROLE_USER))
                .build();

        UserDetails userDetails = UserDetailsImpl
                .builder()
                .authUser(user)
                .build();

        var expiredToken = Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis() - 2000))
                .expiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
                .compact();

        System.out.println("Expired Token: " + expiredToken);

        assertThrows(ExpiredJwtException.class,
                () -> jwtService.isTokenValid(expiredToken, userDetails),
                "Deve lançar uma exceção ExpiredJwtException para token expirado");
    }

}
