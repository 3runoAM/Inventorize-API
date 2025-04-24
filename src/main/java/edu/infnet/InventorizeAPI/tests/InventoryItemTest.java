package edu.infnet.InventorizeAPI.tests;

import edu.infnet.InventorizeAPI.entities.InventoryItem;
import edu.infnet.InventorizeAPI.services.InventoryItemService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Component
public class InventoryItemTest implements ApplicationRunner {
    private InventoryItemService inventoryItemService;

    public InventoryItemTest(InventoryItemService inventoryItemService) {
        this.inventoryItemService = inventoryItemService;
    }

    @Override
    public void run(ApplicationArguments args) {
        try{
            Files.readAllLines(Path.of("src/main/resources/csv/inventory_items_data.csv"))
                    .forEach(line -> {
                                String[] data = line.split(",");
                                InventoryItem inventoryItem = inventoryItemService.processInventoryItemData(data);
                                inventoryItemService.saveInventoryItem(inventoryItem);
                            }
                    );

            System.out.println("#ITENS DE INVENTÁRIO");
            inventoryItemService.getAllInventoryItems().forEach(System.out::println);
        } catch (FileNotFoundException ex) {
            System.out.println("Arquivo não encontrado: " + ex.getMessage());
        }
        catch (IOException ex) {
            System.out.println("Erro ao ler o arquivo: " + ex.getMessage());
        }
    }
}