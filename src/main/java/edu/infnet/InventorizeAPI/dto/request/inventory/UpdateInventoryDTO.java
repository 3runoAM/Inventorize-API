package edu.infnet.InventorizeAPI.dto.request.inventory;

import edu.infnet.InventorizeAPI.entities.Inventory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record UpdateInventoryDTO(
        @NotBlank @Length(max = 50)String name,
        @NotNull @Length(max = 200) String description,
        @NotBlank @Email String notificationEmail
) {
    public static InventoryDTO from(Inventory inventory) {
        return new InventoryDTO(
                inventory.getName(),
                inventory.getDescription(),
                inventory.getNotificationEmail()
        );
    }
}