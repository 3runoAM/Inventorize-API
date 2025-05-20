package edu.infnet.InventorizeAPI.dto.response;

import edu.infnet.InventorizeAPI.entities.Product;
import java.util.UUID;

public record ProductResponseDTO(
        UUID id,
        String name,
        String supplierCode
) {
     public static ProductResponseDTO fromProduct(Product savedProduct){
         return new ProductResponseDTO(
                 savedProduct.getId(),
                 savedProduct.getName(),
                 savedProduct.getSupplierCode()
         );
     }
}
