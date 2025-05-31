package edu.infnet.InventorizeAPI.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record ItemRequestDTO(
        @NotNull UUID productId,
        @NotNull UUID inventoryId,
        @NotNull @PositiveOrZero int currentQuantity,
        @NotNull @PositiveOrZero int minimumStockLevel
) {}
