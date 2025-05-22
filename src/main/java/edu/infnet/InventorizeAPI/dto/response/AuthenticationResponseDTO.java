package edu.infnet.InventorizeAPI.dto.response;

import edu.infnet.InventorizeAPI.entities.AuthUser;

import java.util.UUID;

public record AuthenticationResponseDTO(
        String token,
        String email,
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