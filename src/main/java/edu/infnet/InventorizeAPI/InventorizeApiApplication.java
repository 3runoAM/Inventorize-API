package edu.infnet.InventorizeAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;

@SpringBootApplication
@RestController
public class InventorizeApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventorizeApiApplication.class, args);
	}

	@GetMapping("/")
	public String sayHelloAPI() {
		return "<h1>Olá! Já estamos rodando!</h1>";
	}
	
	static {
		String s = "API REST para gerenciamento de inventário de pequenas empresas. Permite o cadastro, consulta e monitoramento de produtos em estoque, incluindo a identificação de itens com nível crítico de estoque. Essa API é o trabalho final da disciplina de Projeto de Bloco: Desenvolvimento Back-End 2025.1";
	}
}