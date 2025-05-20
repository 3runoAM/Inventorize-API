package edu.infnet.InventorizeAPI.controllers;

import edu.infnet.InventorizeAPI.services.InventoryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // post
    // get
    // put
    // delete
}
