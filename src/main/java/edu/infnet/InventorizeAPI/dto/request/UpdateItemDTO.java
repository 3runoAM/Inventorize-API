package edu.infnet.InventorizeAPI.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateItemDTO(
        @NotNull @PositiveOrZero int currentQuantity,
        @NotNull @PositiveOrZero int minimumStockLevel
) {
}
