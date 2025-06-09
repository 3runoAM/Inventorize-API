package edu.infnet.InventorizeAPI.dto.response;

import edu.infnet.InventorizeAPI.entities.AuthUser;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

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