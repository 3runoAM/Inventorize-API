package edu.infnet.InventorizeAPI.dto.response;

import edu.infnet.InventorizeAPI.entities.Inventory;

import java.util.UUID;

public record InventoryResponseDTO(
        UUID inventoryId,
        String name,
        String description,
        String notificationEmail,
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
