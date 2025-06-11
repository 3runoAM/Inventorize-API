package edu.infnet.inventorize.dto.response;

import edu.infnet.inventorize.entities.AuthUser;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "DTO para resposta de informações dos usuários no sistema")
public record UserResponseDTO(
        @Schema(
                description = "O ID do usuário",
                example = "fe98bbec-723c-497e-9485-366d189bfd37"
        )
        UUID id,

        @Schema(
                description = "O e-mail do usuário",
                example = "userNAME@email.com"
        )
        String email
) {
    public static UserResponseDTO from(AuthUser user) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail());
    }
}