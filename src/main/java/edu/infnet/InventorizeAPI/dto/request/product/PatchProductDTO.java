package edu.infnet.InventorizeAPI.dto.request.product;


import org.hibernate.validator.constraints.Length;

public record PatchProductDTO(
        @Length(min = 2, max = 100) String name,
        @Length(min = 3, max = 100) String supplierCode
) {}