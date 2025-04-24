package edu.infnet.InventorizeAPI.services;

import edu.infnet.InventorizeAPI.entities.InventoryItem;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InventoryItemService {
    private final Map<UUID, InventoryItem> inventoryItemMap;

    public InventoryItemService() {
        this.inventoryItemMap = new HashMap<>();
    }

    public InventoryItem processInventoryItemData(String[] data) {
        return InventoryItem.builder()
                .id(UUID.fromString(data[0]))
                .currentQuantity(Integer.parseInt(data[3]))
                .lowStockLimit(Integer.parseInt(data[4]))
                .build();
    }

    public void saveInventoryItem(InventoryItem inventoryItem) {
        inventoryItemMap.put(inventoryItem.getId(), inventoryItem);
    }

    public List<InventoryItem> getAllInventoryItems() {
        return new ArrayList<>(inventoryItemMap.values());
    }
}
