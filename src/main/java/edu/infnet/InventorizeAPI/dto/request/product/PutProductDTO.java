package edu.infnet.InventorizeAPI.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record PutProductDTO(
        @NotBlank @Length(max = 100) String newName,
        @NotBlank @Length(max = 100) String newSupplierCode
) {}