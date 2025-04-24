package edu.infnet.InventorizeAPI.repositories.map;

import edu.infnet.InventorizeAPI.entities.Inventory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class InventoryMapRepository {
    private final Map<UUID, Inventory> inventoryMap;

    public InventoryMapRepository() {
        this.inventoryMap = new java.util.HashMap<>();
    }

    public void save(Inventory inventory) {
        inventoryMap.put(inventory.getId(), inventory);
    }

    public List<Inventory> findAll() {
        return new ArrayList<>(inventoryMap.values());
    }
}
