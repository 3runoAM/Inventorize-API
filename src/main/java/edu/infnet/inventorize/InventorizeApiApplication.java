package edu.infnet.inventorize;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@RequiredArgsConstructor
public class InventorizeApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(InventorizeApiApplication.class, args);
	}
}