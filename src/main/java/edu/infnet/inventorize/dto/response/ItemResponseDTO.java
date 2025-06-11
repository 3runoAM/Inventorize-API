package edu.infnet.inventorize.dto.response;

import edu.infnet.inventorize.entities.Item;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "DTO para resposta de item no inventário")
public record ItemResponseDTO(
        @Schema(
                description = "O ID do item",
                example = "edab2541-bfea-4bc1-b410-d4f73039f511"
        )
        UUID id,

        @Schema(
                description = "O ID do produto associado ao item",
                example = "3ead5bcf-0c7c-480f-8f6d-0529cdd037ce"
        )
        UUID productId,

        @Schema(
                description = "O ID do inventário ao qual o item pertence",
                example = "917e69df-1b68-4e7a-abd5-551cfbe3ac76"
        )
        UUID inventoryId,

        @Schema(
                description = "A quantidade atual do item no inventário",
                example = "50"
        )
        int currentQuantity,

        @Schema(
                description = "O nível mínimo de estoque do item",
                example = "5"
        )
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