package edu.infnet.InventorizeAPI.dto.request.inventory;

import edu.infnet.InventorizeAPI.entities.Inventory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record UpdateInventoryDTO(
        @Schema(
                description = "Nome do inventário",
                examples = {
                        "Tintas acrílicas",
                        "Pincéis de cerdas naturais",
                        "Canvas de algodão - diferentes tamanhos"
                },
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "O nome do inventário é obrigatório nesse contexto")
        @Length(max = 50)
        String name,

        @Schema(
                description = "A descrição do inventário",
                examples = {
                        "Inventário de tintas acrílicas para pintura artística",
                        "Pincéis de cerdas naturais para pintura a óleo",
                        "Canvas de algodão em diferentes tamanhos para pintura"
                },
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "A descrição do inventário é obrigatória neste contexto")
        @Length(max = 200)
        String description,

        @Schema(
                description = "O e-mail para notificações relacionadas ao inventário",
                examples = {
                        "examploUser@email.com",
                        "emailEmail@user.com"
                },
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "O e-mail para notificações é obrigatório neste contexto")
        @Email(message = "O e-mail para notificações deve ser válido")
        String notificationEmail
) {
    public static UpdateInventoryDTO from(Inventory inventory) {
        return new UpdateInventoryDTO(
                inventory.getName(),
                inventory.getDescription(),
                inventory.getNotificationEmail()
        );
    }
}