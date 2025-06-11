package edu.infnet.inventorize.dto.response;

import edu.infnet.inventorize.entities.AuthUser;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "DTO para resposta de autenticação de usuários no sistema")
public record AuthenticationResponseDTO(
        @Schema(
                description = "Token de autenticação do usuário",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.cThIIoDvwdueQB468K5xDc5633seEFoqwxjF_xSJyQQ"
        )
        String token,

        @Schema(
                description = "E-mail do usuário autenticado",
                example = "user_name@email.com"
        )
        String email,

        @Schema(
                description = "ID do usuário autenticado",
                example = "07cb3d99-e7f5-4d2f-b329-dd60711a5684"
        )
        UUID userId
) {
    public static AuthenticationResponseDTO from(String token, AuthUser user) {
        return new AuthenticationResponseDTO(
                token,
                user.getEmail(),
                user.getId()
        );
    }
}