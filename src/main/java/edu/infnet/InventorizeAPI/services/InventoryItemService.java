package edu.infnet.InventorizeAPI.services;

import edu.infnet.InventorizeAPI.repository.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class InventoryItemService {
    private final InventoryItemRepository inventoryItemRepository;

}