package edu.infnet.InventorizeAPI.dto.request;

import edu.infnet.InventorizeAPI.entities.InventoryItem;
import jakarta.validation.constraints.PositiveOrZero;


public record PatchItemRequestDTO(
        @PositiveOrZero Integer currentQuantity,
        @PositiveOrZero Integer minimumStockLevel)
{
    public static PatchItemRequestDTO from(InventoryItem item) {
        return new PatchItemRequestDTO(
                item.getCurrentQuantity(),
                item.getMinimumStockLevel()
        );
    }
}