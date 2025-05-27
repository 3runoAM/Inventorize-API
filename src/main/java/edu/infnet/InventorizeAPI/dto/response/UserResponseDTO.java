package edu.infnet.InventorizeAPI.dto.response;

import edu.infnet.InventorizeAPI.entities.AuthUser;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String email
) {
    public static UserResponseDTO from(AuthUser user) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail());
    }
}