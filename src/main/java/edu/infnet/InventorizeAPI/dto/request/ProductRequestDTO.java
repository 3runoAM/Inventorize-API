package edu.infnet.InventorizeAPI.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public record ProductRequestDTO(
        @NotBlank @Column(length = 100) String name,
        @Column(length = 100) String supplierCode
) {}