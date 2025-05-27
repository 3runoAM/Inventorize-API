package edu.infnet.InventorizeAPI.controllers;

import edu.infnet.InventorizeAPI.dto.request.InventoryRequestDTO;
import edu.infnet.InventorizeAPI.dto.response.InventoryResponseDTO;
import edu.infnet.InventorizeAPI.services.InventoryService;
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
@RequestMapping("/inventories")
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponseDTO> createInventory(@Valid @RequestBody InventoryRequestDTO inventoryRequestDTO) {
        InventoryResponseDTO newInventory = inventoryService.createInventory(inventoryRequestDTO);

        return ResponseEntity.ok(newInventory);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponseDTO> getInventory(@PathVariable UUID id) {
        InventoryResponseDTO inventory = inventoryService.getById(id);

        return ResponseEntity.ok(inventory);
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponseDTO>> getAllInventories() {
        List<InventoryResponseDTO> inventories = inventoryService.getAll();

        return ResponseEntity.ok(inventories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponseDTO> putInventory(@PathVariable UUID id, @Valid @RequestBody InventoryRequestDTO inventoryRequestDTO) {
        InventoryResponseDTO updatedInventory = inventoryService.update(id, inventoryRequestDTO);

        return ResponseEntity.ok(updatedInventory);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<InventoryResponseDTO> patchInventory(@PathVariable UUID id, @Valid @RequestBody InventoryRequestDTO inventoryRequestDTO) {
        InventoryResponseDTO updatedInventory = inventoryService.patch(id, inventoryRequestDTO);

        return ResponseEntity.ok(updatedInventory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        inventoryService.delete(id);

        return ResponseEntity.ok("| Inventário deletado com sucesso.");
    }
}