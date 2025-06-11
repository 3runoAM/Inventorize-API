package edu.infnet.inventorize.dto.request.inventory;

import edu.infnet.inventorize.entities.Inventory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

@Schema(description = "DTO para atualização total de um inventário existente, permitindo a modificação de todos os " +
        "campos como nome, descrição e e-mail de notificação.")
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
        @Length(max = 50, message = "O nome do inventário deve ter no máximo 50 caracteres")
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
        @Length(max = 200, message = "A descrição do inventário deve ter no máximo 200 caracteres")
        String description,

        @Schema(
                description = "O e-mail para notificações relacionadas ao inventário",
                examples = {
                        "examploUser@email.com",
                        "emailEmail@user.com"
                },
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "O email para notificações é obrigatório neste contexto")
        @Email(message = "O email para notificações deve ser válido")
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