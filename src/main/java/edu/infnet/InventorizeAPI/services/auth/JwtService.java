package edu.infnet.InventorizeAPI.services.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    private
    SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Gera um token JWT para o usuário especificado.
     *
     * @param userDetails detalhes do usuário para o qual o token será gerado
     * @return String contendo o token JWT gerado
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * Verifica se um token é válido para um determinado usuário.
     *
     * @param token token JWT a ser validado
     * @return true se o token for válido, false caso contrário
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Obtém o nome de usuário contido no token JWT.
     *
     * @param token token JWT a ser lido
     * @return String contendo o username
     */
    public String getUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Verifica se o token está expirado.
     *
     * @param token token JWT a ser verificado
     * @return true se o token estiver expirado, false caso contrário
     */
    private boolean isTokenExpired(String token) {
        return getExpirationDate(token).before(new Date());
    }

    /**
     * Obtém a data de expiração contida no token.
     *
     * @param token token JWT a ser lido
     * @return Date contendo a data de expiração
     */
    private Date getExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrai uma claim específica do token usando uma função de resolução.
     *
     * @param token token JWT do qual será extraída a claim
     * @param claimsResolver função para resolver o tipo específico da claim
     * @return T valor da claim convertido para o tipo especificado
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Obtém todas as claims do token JWT.
     *
     * @param token token JWT do qual serão extraídas as claims
     * @return Claims objeto contendo todas as claims do token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}