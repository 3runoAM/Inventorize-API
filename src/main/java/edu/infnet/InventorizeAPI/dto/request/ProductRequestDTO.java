package edu.infnet.InventorizeAPI.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record ProductRequestDTO(
        @NotBlank @Length(max = 100) String name,
        @Length(max = 100) String supplierCode
) {}