package edu.infnet.inventorize.dto.request.item;

import edu.infnet.inventorize.entities.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "DTO para atualização parcial de um item no inventário")
public record PatchItemDTO(
        @Schema(
                description = "A quantidade atual do item no inventário",
                examples = {
                        "10",
                        "5",
                        "1"
                })
        @PositiveOrZero(message = "A quantidade atual do item deve ser zero ou positiva")
        Integer currentQuantity,

        @Schema(
                description = "O limite crítico de estoque do item",
                examples = {
                        "10",
                        "5",
                        "1"
                })
        @PositiveOrZero(message = "O limite crítico de estoque do item deve ser zero ou positivo")
        Integer minimumStockLevel
) {
    public static PatchItemDTO from(Item item) {
        return new PatchItemDTO(
                item.getCurrentQuantity(),
                item.getMinimumStockLevel()
        );
    }
}