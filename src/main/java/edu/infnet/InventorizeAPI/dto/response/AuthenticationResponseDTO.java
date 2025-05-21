package edu.infnet.InventorizeAPI.dto.response;

import edu.infnet.InventorizeAPI.entities.AuthUser;

public record AuthenticationResponseDTO(
        String token,
        String email
) {
    public static AuthenticationResponseDTO from(String token, AuthUser user) {
        return new AuthenticationResponseDTO(
                token,
                user.getEmail()
        );
    }
}