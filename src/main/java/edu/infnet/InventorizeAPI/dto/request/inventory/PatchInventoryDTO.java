package edu.infnet.InventorizeAPI.dto.request.inventory;

import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;

public record PatchInventoryDTO(
        @Length(max = 50) String name,
        @Length(max = 200) String description,
        @Email String notificationEmail
) { }