package edu.infnet.InventorizeAPI.dto.request.inventoryItem;

import edu.infnet.InventorizeAPI.entities.Item;
import jakarta.validation.constraints.PositiveOrZero;


public record PatchItemDTO(
        @PositiveOrZero Integer currentQuantity,
        @PositiveOrZero Integer minimumStockLevel)
{
    public static PatchItemDTO from(Item item) {
        return new PatchItemDTO(
                item.getCurrentQuantity(),
                item.getMinimumStockLevel()
        );
    }
}