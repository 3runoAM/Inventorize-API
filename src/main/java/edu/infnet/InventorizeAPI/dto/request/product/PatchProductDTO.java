package edu.infnet.InventorizeAPI.dto.request.product;


import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.Length;

@Schema(description = "DTO para atualização parcial de um produto")
public record PatchProductDTO(
        @Schema(
                name = "name",
                description = "Nome do produto",
                examples = {
                        "Cola Quente para Tecido - Bastão 20cm",
                        "Molde para Capas de Livro A5 - Coração",
                        "Fita de Cetim 5cm - Vermelho Vinho"
                }
        )
        @Length(min = 2, max = 100)
        String name,

        @Schema(
                name = "supplierCode",
                description = "Código do fornecedor do produto",
                examples = {
                        "CODIGO-001",
                        "Dona Lúcia +551191234-5678",
                        "Loja de Materiais Artísticos, Rua das Flores, 12345",
                }
        )
        @Length(min = 3, max = 100)
        String supplierCode
) {}