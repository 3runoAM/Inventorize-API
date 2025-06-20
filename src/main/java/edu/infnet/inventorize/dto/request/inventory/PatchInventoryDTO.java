package edu.infnet.inventorize.dto.request.inventory;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;

@Schema(description = "DTO para atualização parcial de um inventário existente, permitindo a modificação de campos " +
        "específicos como nome, descrição e e-mail de notificação.")
public record PatchInventoryDTO(
        @Schema(
                name = "name",
                description = "Nome do inventário",
                examples = {
                        "Tintas acrílicas",
                        "Pincéis de cerdas naturais",
                        "Canvas de algodão - diferentes tamanhos"
                }
        )
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
                description = "O e-mail para notificações de estoque baixo do inventário",
                example = "email@email.com"
        )
        @Email(message = "O e-mail para notificações deve ser válido")
        String notificationEmail
) {}