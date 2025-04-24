package edu.infnet.InventorizeAPI.tests;

import edu.infnet.InventorizeAPI.entities.Inventory;
import edu.infnet.InventorizeAPI.services.InventoryService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class InventoryTest implements ApplicationRunner {
    private final InventoryService inventoryService;

    public InventoryTest(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            Files.readAllLines(Path.of("src/main/resources/csv/inventories_data.csv"))
                    .forEach(line -> {
                                String[] data = line.split(",");
                                Inventory inventory = inventoryService.processInventoryData(data);
                                inventoryService.saveInventory(inventory);
                            }
                    );

            System.out.println("#INVENTÁRIOS");
            inventoryService.getAllInventories().forEach(System.out::println);
        } catch (FileNotFoundException ex) {
            System.out.println("Arquivo não encontrado: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Erro ao ler o arquivo: " + ex.getMessage());
        }
    }
}