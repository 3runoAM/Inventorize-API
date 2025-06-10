package edu.infnet.InventorizeAPI.dto.request.item;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "DTO para atualização total de um item no inventário")
public record UpdateItemDTO(
        @Schema(
                description = "A quantidade atual do item no inventário",
                examples = {
                        "10",
                        "5",
                        "1"
                },
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "A quantidade atual do item é obrigatória neste contexto")
        @PositiveOrZero(message = "A quantidade atual do item deve ser zero ou positiva")
        Integer currentQuantity,

        @Schema(
                description = "O limite crítico de estoque do item",
                examples = {
                        "10",
                        "5",
                        "1"
                },
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "O limite crítico de estoque do item é obrigatório neste contexto")
        @PositiveOrZero(message = "O limite crítico de estoque do item deve ser zero ou positivo")
        Integer minimumStockLevel
) {}