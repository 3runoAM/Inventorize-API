package edu.infnet.inventorize.dto.request.product;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Schema(description = "DTO para criação de um novo produto")
public record ProductDTO(
        @Schema(
                description = "O nome do produto,",
                examples = {
                        "Tinta Acrílica Dourada Pérola - 50ml",
                        "Argila Polimérica Biscuit Branca - 500g",
                        "Fita de Cetim 5cm - Vermelho Vinho",
                },
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "O nome do produto é obrigatório")
        @Length(max = 100, message = "O nome do produto deve ter no máximo 100 caracteres")
        String name,

        @Schema(
                description = "Código do fornecedor do produto",
                examples = {
                        "CODIGO-001",
                        "Dona Lúcia +551191234-5678",
                        "Loja de Materiais Artísticos, Rua das Flores, 12345",
                },
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "O código do fornecedor é obrigatório")
        @Length(max = 100, message = "O código do fornecedor deve ter no máximo 100 caracteres")
        String supplierCode
) {}
