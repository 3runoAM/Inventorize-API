package edu.infnet.InventorizeAPI.dto.response;

import edu.infnet.InventorizeAPI.entities.InventoryItem;

import java.util.UUID;

public record ItemResponseDTO(
        UUID id,
        UUID productId,
        UUID inventoryId,
        int currentQuantity,
        int lowStockLimit
) {
    public static ItemResponseDTO from(InventoryItem item) {
        return new ItemResponseDTO(
                item.getId(),
                item.getProduct().getId(),
                item.getInventory().getId(),
                item.getCurrentQuantity(),
                item.getLowStockLimit()
        );
    }
}