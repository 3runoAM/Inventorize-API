package edu.infnet.InventorizeAPI.controllers;

import edu.infnet.InventorizeAPI.services.InventoryItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/inventoryItem")
public class InventoryItemController {
    private final InventoryItemService inventoryItemService;


}
