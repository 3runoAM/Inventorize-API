package edu.infnet.InventorizeAPI.services;

import edu.infnet.InventorizeAPI.entities.Inventory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InventoryService {
    private final Map<UUID, Inventory> inventoryMap;

    public InventoryService() {
        this.inventoryMap = new HashMap<>();
    }

    public void saveInventory(Inventory inventory) {
        inventoryMap.put(inventory.getId(), inventory);
    }

    public List<Inventory> getAllInventories() {
        return new ArrayList<>(inventoryMap.values());
    }

    public Inventory processInventoryData(String[] data) {
        return Inventory.builder()
                .id(UUID.fromString(data[0]))
                .name(data[1])
                .description(data[2])
                .notificationEmail(data[3])
                .build();
    }
}
