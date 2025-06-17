package edu.infnet.inventorize.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(
                new Info()
                        .title("Inventorize API")
                        .version("1.0.0")
                        .description(" API REST para gerenciamento de inventário de pequenas empresas e artistas" +
                                "independentes. Permite o cadastro, consulta e monitoramento de produtos em " +
                                "estoque, incluindo a identificação e notificação de itens com nível crítico de" +
                                " estoque. Essa API é o trabalho final da disciplina de Projeto de Bloco: " +
                                "Desenvolvimento Back-End 2025.1")
        ).components(
                new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"))
        );
    }
}
