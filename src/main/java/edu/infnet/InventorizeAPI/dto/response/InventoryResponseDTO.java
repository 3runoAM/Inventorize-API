package edu.infnet.InventorizeAPI.dto.response;

import edu.infnet.InventorizeAPI.entities.Inventory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record InventoryResponseDTO(
        @Schema(
                description = "ID do inventário",
                example = "7c798d79-958e-4742-bfd4-c46f3c7b16c8"
        )
        UUID id,
        @Schema(
                description = "Nome do inventário",
                example = "Tintas acrílicas"
        )
        String name,
        @Schema(
                description = "Descrição do inventário",
                example = "Inventário de tintas acrílicas para pintura artística"
        )
        String description,

        @Schema(
                description = "E-mail para notificações relacionadas ao inventário",
                example = "aviso@email.com"
        )
        String notificationEmail,

        @Schema(
                description = "ID do proprietário do inventário",
                example = "d66a1dd1-cdd7-4046-9d7c-0293f1fa790b"
        )
        UUID ownerId
) {
    public static InventoryResponseDTO from(Inventory inventory) {
        return new InventoryResponseDTO(
                inventory.getId(),
                inventory.getName(),
                inventory.getDescription(),
                inventory.getNotificationEmail(),
                inventory.getOwner().getId()
        );
    }
}
