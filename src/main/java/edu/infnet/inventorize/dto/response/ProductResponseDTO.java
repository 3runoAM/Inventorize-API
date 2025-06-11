package edu.infnet.inventorize.dto.response;

import edu.infnet.inventorize.entities.Product;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "DTO para resposta de produto no sistema")
public record ProductResponseDTO(
        @Schema(
                description = "O ID do produto",
                example = "13892e7d-3e7e-4c99-be47-0b9f053f13a6"
        )
        UUID productId,

        @Schema(
                description = "O ID do proprietário do produto",
                example = "d0890330-3cd0-4f42-a7e5-6ee8c1f4c964"
        )
        UUID ownerId,

        @Schema(
                description = "O nome do produto",
                example = "Pincel de cerdas naturais"
        )
        String name,

        @Schema(
                description = "O código do fornecedor do produto",
                example = "SUP12345"
        )
        String supplierCode
) {
     public static ProductResponseDTO fromProduct(Product product){
         return new ProductResponseDTO(
                 product.getId(),
                 product.getOwner().getId(),
                 product.getName(),
                 product.getSupplierCode()
         );
     }
}
