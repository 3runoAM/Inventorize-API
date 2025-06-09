package edu.infnet.InventorizeAPI.dto.request.item;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record ItemDTO(
        @Schema(
                description = "O id do produto associado ao item",
                examples = {
                        "239d84f6-9117-41a2-b7b7-aeccece01aa4",
                        "72c21b5f-c4b5-465d-9ad4-e24d9f60f03f",
                        "3ac44bf6-f199-4f41-9ef7-a63b4442f533"
                },
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "O id do produto não pode ser nulo")
        UUID productId,

        @Schema(
                description = "O id do inventário associado ao item",
                examples = {
                        "9eaec3da-fc66-41d0-848a-a99ebbef8d83",
                        "1ebca300-f659-41f8-bada-bee3782890fc",
                        "287a2378-1203-4723-a83c-f8bbe6d73dac"
                },
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "O id do inventário não pode ser nulo")
        UUID inventoryId,

        @Schema(
                description = "A quantidade atual do item no inventário",
                examples = {
                        "10",
                        "5",
                        "1"
                },
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "A quantidade atual do item não pode ser nula")
        @PositiveOrZero(message = "A quantidade atual do item deve ser zero ou positiva")
        int currentQuantity,

        @Schema(
                description = "O limite crítico de estoque do item",
                examples = {
                        "10",
                        "5",
                        "1"
                },
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "O limite crítico de estoque do item não pode ser nulo")
        @PositiveOrZero(message = "O limite crítico de estoque do item deve ser zero ou positivo")
        int minimumStockLevel
) {}
