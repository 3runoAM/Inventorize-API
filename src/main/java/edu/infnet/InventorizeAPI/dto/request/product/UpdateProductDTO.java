package edu.infnet.InventorizeAPI.dto.request.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Schema(description = "DTO para atualização total de um produto")
public record UpdateProductDTO(
        @Schema(name = "name",
                description = "O nome do produto,",
                examples = {
                        "Tinta Acrílica Dourada Pérola - 50ml",
                        "Argila Polimérica Biscuit Branca - 500g",
                        "Fita de Cetim 5cm - Vermelho Vinho"
                },
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "O novo nome é obrigatório nesse contexto")
        @Length(max = 100, message = "O novo nome deve ter 100 caracteres no máximo")
        String newName,

        @Schema(name = "supplierCode",
                description = "O código do fornecedor do produto",
                examples = {
                        "CODIGO-001",
                        "Dona Lúcia +551191234-5678",
                        "Loja de Materiais Artísticos, Rua das Flores, 12345"
                },
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "O novo código de fornecedor é obrigatório nesse contexto")
        @Length(max = 100, message = "O novo código de fornecedor deve ter 100 caracteres no máximo")
        String newSupplierCode
) {}