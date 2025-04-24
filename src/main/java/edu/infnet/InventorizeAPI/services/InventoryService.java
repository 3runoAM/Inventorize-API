package edu.infnet.InventorizeAPI.services;

import edu.infnet.InventorizeAPI.entities.Inventory;
import edu.infnet.InventorizeAPI.repositories.map.InventoryMapRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class InventoryService {
    private final InventoryMapRepository inventoryMapRepository;

    public InventoryService(InventoryMapRepository inventoryMapRepository) {
        this.inventoryMapRepository = inventoryMapRepository;
    }

    public void processData(String[] data) {
        Inventory inventory = Inventory.builder()
                .id(UUID.fromString(data[0]))
                .name(data[1])
                .description(data[2])
                .notificationEmail(data[3])
                .build();

        inventoryMapRepository.save(inventory);
    }

    public void saveInventory(Inventory inventory) {
        inventoryMapRepository.save(inventory);
    }

    public List<Inventory> getAllInventories() {
        return inventoryMapRepository.findAll();
    }
}
