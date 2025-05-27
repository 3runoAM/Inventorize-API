package edu.infnet.InventorizeAPI.dto.request;

import edu.infnet.InventorizeAPI.entities.Inventory;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InventoryRequestDTO(
        @NotBlank @Column(length = 50)String name,
        @Column(length = 200) String description,
        @Email @NotBlank String notificationEmail
) {
    public static InventoryRequestDTO from(Inventory inventory) {
        return new InventoryRequestDTO(
                inventory.getName(),
                inventory.getDescription(),
                inventory.getNotificationEmail()
        );
    }
}