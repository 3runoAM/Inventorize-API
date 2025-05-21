package edu.infnet.InventorizeAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class InventorizeApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(InventorizeApiApplication.class, args);
	}

	@GetMapping("/")
	public String sayHelloAPI() {
		return "<h1>Olá! Já estamos rodando!</h1>";
	}
}