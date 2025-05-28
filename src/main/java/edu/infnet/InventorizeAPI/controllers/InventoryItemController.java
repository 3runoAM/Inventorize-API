package edu.infnet.InventorizeAPI.controllers;

import edu.infnet.InventorizeAPI.dto.request.ItemRequestDTO;
import edu.infnet.InventorizeAPI.dto.response.ItemResponseDTO;
import edu.infnet.InventorizeAPI.services.InventoryItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/inventoryItems")
public class InventoryItemController {
    private final InventoryItemService inventoryItemService;

    @PostMapping
    public ResponseEntity<ItemResponseDTO> createInventoryItem(@Valid @RequestBody ItemRequestDTO ItemRequest) {
        ItemResponseDTO inventoryItemInfo = inventoryItemService.create(ItemRequest);

        return ResponseEntity.ok(inventoryItemInfo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> getInventoryItem(@PathVariable UUID id) {
        ItemResponseDTO inventoryItemInfo = inventoryItemService.getById(id);

        return ResponseEntity.ok(inventoryItemInfo);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDTO>> getInventoryItems() {
        List<ItemResponseDTO> items = inventoryItemService.getAll();

        return ResponseEntity.ok(items);
    }

    @GetMapping("/{inventoryId}")
    public ResponseEntity<List<ItemResponseDTO>> getAllByInventory(@PathVariable UUID inventoryId) {
        List<ItemResponseDTO> items = inventoryItemService.getAllByInventoryId(inventoryId);

        return ResponseEntity.ok(items);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> updateInventoryItem(@PathVariable UUID id, @Valid @RequestBody ItemRequestDTO itemRequest) {
        ItemResponseDTO updatedItem = inventoryItemService.update(id, itemRequest);

        return ResponseEntity.ok(updatedItem);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> patchInventoryItem(@PathVariable UUID id, @Valid @RequestBody ItemRequestDTO itemRequest) {
        ItemResponseDTO patchedItem = inventoryItemService.patch(id, itemRequest);

        return ResponseEntity.ok(patchedItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInventoryItem(@PathVariable UUID id) {
        inventoryItemService.deleteById(id);

        return ResponseEntity.ok("Item deletado com sucesso");
    }
}
