package edu.infnet.InventorizeAPI.services;

import edu.infnet.InventorizeAPI.entities.InventoryItem;
import edu.infnet.InventorizeAPI.repository.InventoryItemRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InventoryItemService {
    private final InventoryItemRepository inventoryItemRepository;

    public InventoryItemService(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
    }
}
