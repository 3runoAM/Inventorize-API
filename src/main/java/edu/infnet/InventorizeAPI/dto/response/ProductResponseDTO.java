package edu.infnet.InventorizeAPI.dto.response;

import edu.infnet.InventorizeAPI.entities.Product;
import java.util.UUID;

public record ProductResponseDTO(
        UUID id,
        String name,
        String supplierCode
) {
     public static ProductResponseDTO fromProduct(Product product){
         return new ProductResponseDTO(
                 product.getId(),
                 product.getName(),
                 product.getSupplierCode()
         );
     }
}
