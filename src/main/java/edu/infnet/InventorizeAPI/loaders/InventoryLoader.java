package edu.infnet.InventorizeAPI.loaders;

import edu.infnet.InventorizeAPI.services.InventoryService;
import jakarta.annotation.PostConstruct;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Order(2)
public class InventoryLoader {
    private final InventoryService inventoryService;

    public InventoryLoader(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostConstruct
    public void loadData() throws IOException {
        Files.readAllLines(Path.of("src/main/resources/csv/inventories_data.csv"))
            .forEach(line -> {
                String[] data = line.split(",");
                inventoryService.processData(data);
            }
        );

        inventoryService.getAllInventories().forEach(System.out::println);
    }
}