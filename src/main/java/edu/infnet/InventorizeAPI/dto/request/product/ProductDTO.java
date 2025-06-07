package edu.infnet.InventorizeAPI.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record ProductDTO(
        @NotBlank @Length(max = 100) String name,
        @Length(max = 100) String supplierCode
) {}