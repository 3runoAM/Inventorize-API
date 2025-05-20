package edu.infnet.InventorizeAPI.controllers;

import edu.infnet.InventorizeAPI.services.InventoryItemService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventoryItem")
public class InventoryItemController {
    private final InventoryItemService inventoryItemService;

    public InventoryItemController(InventoryItemService inventoryItemService) {
        this.inventoryItemService = inventoryItemService;
    }
}
