package edu.infnet.InventorizeAPI.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationRequestDTO(
        @Schema(
                name = "email",
                description = "Email do usuário para autenticação, único para casa usuário",
                examples = {
                        "user_mail@gmail.com",
                        "emailUser@outlook.com"
                },
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Email(message = "O email deve ser válido")
        @NotBlank(message = "Email não pode ser vazio")
        String email,

        @Schema(
                name = "password",
                description = "Senha do usuário para autenticação, deve ter entre 8 e 16 caracteres",
                examples = {
                        "Senha123!",
                        "SenhaSegura456"
                },
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Senha não pode ser vazia")
        @Size(min=8, max=16, message="Senha deve ter entre 8 e 16 caracteres")
        String password
) {}