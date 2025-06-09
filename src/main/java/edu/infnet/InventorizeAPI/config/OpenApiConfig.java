package edu.infnet.InventorizeAPI.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Inventorize API")
                        .version("1.0.0")
                        .description("API REST para gerenciamento de inventário de pequenas empresas. Permite o " +
                                "cadastro, consulta e monitoramento de produtos em estoque, incluindo a identificação de" +
                                " itens com nível crítico de estoque. Essa API é o trabalho final da disciplina de " +
                                "Projeto de Bloco: Desenvolvimento Back-End 2025.1"));
    }
}
