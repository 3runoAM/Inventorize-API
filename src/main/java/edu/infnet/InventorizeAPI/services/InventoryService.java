package edu.infnet.InventorizeAPI.services;

import edu.infnet.InventorizeAPI.entities.Inventory;
import edu.infnet.InventorizeAPI.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }
}
