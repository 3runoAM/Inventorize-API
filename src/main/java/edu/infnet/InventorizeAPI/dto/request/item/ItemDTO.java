package edu.infnet.InventorizeAPI.dto.request.item;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record ItemDTO(
        @NotNull UUID productId,
        @NotNull UUID inventoryId,
        @NotNull @PositiveOrZero int currentQuantity,
        @NotNull @PositiveOrZero int minimumStockLevel
) {}
