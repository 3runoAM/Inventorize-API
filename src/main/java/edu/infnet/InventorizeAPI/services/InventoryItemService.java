package edu.infnet.InventorizeAPI.services;

import edu.infnet.InventorizeAPI.dto.request.ItemRequestDTO;
import edu.infnet.InventorizeAPI.dto.response.InventoryResponseDTO;
import edu.infnet.InventorizeAPI.dto.response.ItemResponseDTO;
import edu.infnet.InventorizeAPI.entities.*;
import edu.infnet.InventorizeAPI.exceptions.custom.InventoryItemNotFound;
import edu.infnet.InventorizeAPI.repository.InventoryItemRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class InventoryItemService {
    private final ProductService productService;
    private final InventoryService inventoryService;
    private final AuthenticationService authService;
    private final InventoryItemRepository inventoryItemRepository;

    public ItemResponseDTO create(ItemRequestDTO itemRequest) {
        Product product = productService.validateOwnershipById(itemRequest.productId());
        Inventory inventory = inventoryService.validateOwnershipById(itemRequest.inventoryId());

        var newInventoryItem = InventoryItem.builder()
                .product(product)
                .inventory(inventory)
                .currentQuantity(itemRequest.currentQuantity())
                .lowStockLimit(itemRequest.lowStockLimit())
                .build();

        var savedInventoryItem = inventoryItemRepository.save(newInventoryItem);

        return ItemResponseDTO.from(savedInventoryItem);
    }

    public ItemResponseDTO getById(UUID id) {
        InventoryItem inventoryItem = validateOwnershipById(id);

        return ItemResponseDTO.from(inventoryItem);
    }

    public List<ItemResponseDTO> getAll(){
        var inventoryIds = inventoryService.getAll()
                .stream()
                .map(InventoryResponseDTO::inventoryId)
                .toList();

        return inventoryItemRepository.getAllByInventoryIdIn(inventoryIds)
                .stream()
                .map(ItemResponseDTO::from)
                .toList();
    }

    public List<ItemResponseDTO> getAllByInventoryId(UUID inventoryId) {
        inventoryService.validateOwnershipById(inventoryId);

        List<InventoryItem> items = inventoryItemRepository.getAllByInventoryId(inventoryId);

        return items.stream().map(ItemResponseDTO::from).toList();
    }

    public ItemResponseDTO update(UUID id, @Valid ItemRequestDTO itemRequest) {
        InventoryItem item = validateOwnershipById(id);

        var updatedItem = item.toBuilder()
                .currentQuantity(itemRequest.currentQuantity())
                .lowStockLimit(itemRequest.lowStockLimit())
                .product(item.getProduct())
                .inventory(item.getInventory())
                .build();

        var savedItem = inventoryItemRepository.save(updatedItem);

        return ItemResponseDTO.from(savedItem);
    }

    public ItemResponseDTO patch(UUID id, @Valid ItemRequestDTO itemRequest) {
        InventoryItem item = validateOwnershipById(id);

        var itemBuilder = item.toBuilder();
        if (itemRequest.currentQuantity() > 0) itemBuilder.currentQuantity(itemRequest.currentQuantity());
        if (itemRequest.lowStockLimit() > 0) itemBuilder.lowStockLimit(itemRequest.lowStockLimit());

        itemBuilder.product(item.getProduct());
        itemBuilder.inventory(item.getInventory());

        var updatedItem = itemBuilder.build();
        var savedItem = inventoryItemRepository.save(updatedItem);

        return ItemResponseDTO.from(savedItem);
    }

    public void deleteById(UUID id) {
        InventoryItem inventoryItem = validateOwnershipById(id);
        inventoryItemRepository.delete(inventoryItem);
    }

    // Métodos utilitários
    private InventoryItem validateOwnershipById(UUID inventoryItemId) {
        var inventoryItem = inventoryItemRepository.findById(inventoryItemId).orElseThrow(() -> new InventoryItemNotFound("Item de inventário com o [ ID: %s ] não encontrado".formatted(inventoryItemId)));

        inventoryService.validateOwnershipById(inventoryItem.getInventory().getId());
        productService.validateOwnershipById(inventoryItem.getProduct().getId());

        return inventoryItem;
    }
}