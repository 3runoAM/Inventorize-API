package edu.infnet.InventorizeAPI.dto.request.inventory;

import edu.infnet.InventorizeAPI.entities.Inventory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import org.hibernate.validator.constraints.Length;

public record InventoryDTO(
        @Schema(
                description = "O nome do inventário",
                examples = {
                        "Tintas acrílicas",
                        "Pincéis de cerdas naturais",
                        "Canvas de algodão - diferentes tamanhos"
                },
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "O nome do inventário é obrigatório")
        @Length(max = 50, message = "O nome do inventário deve ter no máximo 50 caracteres")
        String name,

        @Schema(
                description = "A descrição do inventário",
                examples = {
                        "Inventário de tintas acrílicas para pintura artística",
                        "Pincéis de cerdas naturais para pintura a óleo",
                        "Canvas de algodão em diferentes tamanhos para pintura"
                }
        )
        @Length(max = 200, message = "A descrição do inventário deve ter no máximo 200 caracteres")
        String description,

        @Schema(
                description = "O e-mail para notificações relacionadas ao inventário",
                examples = {
                        "examploUser@email.com",
                        "emailEmail@user.com"
                }
        )
        @NotBlank(message = "O e-mail para notificações é obrigatório")
        @Email (message = "O e-mail para notificações deve ser válido")
        String notificationEmail
) {
    public static InventoryDTO from(Inventory inventory) {
        return new InventoryDTO(
                inventory.getName(),
                inventory.getDescription(),
                inventory.getNotificationEmail()
        );
    }
}