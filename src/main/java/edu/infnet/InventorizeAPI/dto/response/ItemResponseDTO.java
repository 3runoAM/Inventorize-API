package edu.infnet.InventorizeAPI.dto.response;

import edu.infnet.InventorizeAPI.entities.Item;

import java.util.UUID;

public record ItemResponseDTO(
        UUID id,
        UUID productId,
        UUID inventoryId,
        int currentQuantity,
        int minimumStockLevel
) {
    public static ItemResponseDTO from(Item item) {
        return new ItemResponseDTO(
                item.getId(),
                item.getProduct().getId(),
                item.getInventory().getId(),
                item.getCurrentQuantity(),
                item.getMinimumStockLevel()
        );
    }
}