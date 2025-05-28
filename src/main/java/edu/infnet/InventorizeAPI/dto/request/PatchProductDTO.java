package edu.infnet.InventorizeAPI.dto.request;


import org.hibernate.validator.constraints.Length;

public record PatchProductDTO(
        @Length(max = 100) String name,
        @Length(max = 100) String supplierCode
) {}