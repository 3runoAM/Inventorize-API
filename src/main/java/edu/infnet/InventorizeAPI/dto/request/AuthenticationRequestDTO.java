package edu.infnet.InventorizeAPI.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO para autenticação de usuários no sistema")
public record AuthenticationRequestDTO(
        @Schema(
                name = "email",
                description = "Email do usuário para autenticação, único para casa usuário",
                example = "user_mail@gmail.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @Email(message = "O email deve ser válido")
        @NotBlank(message = "Email não pode ser vazio")
        String email,

        @Schema(
                name = "password",
                description = "Senha do usuário para autenticação, deve ter entre 8 e 16 caracteres",
                example = "SenhaSegura456",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "A senha não pode ser vazia")
        @Size(min=8, max=16, message="A senha deve ter entre 8 e 16 caracteres")
        String password
) {}