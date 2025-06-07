package edu.infnet.InventorizeAPI.dto.request.inventory;

import edu.infnet.InventorizeAPI.entities.Inventory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record InventoryDTO(
        @NotBlank @Length(max = 50)String name,
        @Length(max = 200) String description,
        @Email @NotBlank String notificationEmail
) {
    public static InventoryDTO from(Inventory inventory) {
        return new InventoryDTO(
                inventory.getName(),
                inventory.getDescription(),
                inventory.getNotificationEmail()
        );
    }
}