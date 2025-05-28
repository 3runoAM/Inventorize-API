package edu.infnet.InventorizeAPI.services;

import edu.infnet.InventorizeAPI.dto.request.ItemRequestDTO;
import edu.infnet.InventorizeAPI.dto.request.PatchItemRequestDTO;
import edu.infnet.InventorizeAPI.dto.request.UpdateItemDTO;
import edu.infnet.InventorizeAPI.dto.response.InventoryResponseDTO;
import edu.infnet.InventorizeAPI.dto.response.ItemResponseDTO;
import edu.infnet.InventorizeAPI.entities.*;
import edu.infnet.InventorizeAPI.exceptions.custom.InventoryItemNotFound;
import edu.infnet.InventorizeAPI.repository.InventoryItemRepository;
import jakarta.transaction.Transactional;
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

    public ItemResponseDTO update(UUID id, @Valid UpdateItemDTO itemRequest) {
        InventoryItem item = validateOwnershipById(id);

        var updatedItem = item.toBuilder()
                .currentQuantity(itemRequest.currentQuantity())
                .lowStockLimit(itemRequest.lowStockLimit())
                .build();

        var savedItem = inventoryItemRepository.save(updatedItem);

        return ItemResponseDTO.from(savedItem);
    }

    public ItemResponseDTO patch(UUID id, @Valid PatchItemRequestDTO itemRequest) {
        InventoryItem item = validateOwnershipById(id);

        var itemBuilder = item.toBuilder();
        if (itemRequest.currentQuantity() != null) itemBuilder.currentQuantity(itemRequest.currentQuantity());
        if (itemRequest.lowStockLimit() != null) itemBuilder.lowStockLimit(itemRequest.lowStockLimit());

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

    @Transactional
    public ItemResponseDTO adjustCurrentQuantity(UUID itemId, int adjustment) {
        var inventoryItem = validateOwnershipWithLock(itemId);

        var newQuantity = Math.max((inventoryItem.getCurrentQuantity() + adjustment), 0);
        var newItem = inventoryItem.toBuilder().currentQuantity(newQuantity).build();
        var updatedItem = inventoryItemRepository.save(newItem);

        return ItemResponseDTO.from(updatedItem);
    }

    // Métodos utilitários
    private InventoryItem validateOwnershipById(UUID inventoryItemId) {
        var inventoryItem = findById(inventoryItemId);

        inventoryService.validateOwnershipById(inventoryItem.getInventory().getId());
        productService.validateOwnershipById(inventoryItem.getProduct().getId());

        return inventoryItem;
    }

    private InventoryItem findById(UUID inventoryItemId) {
        return inventoryItemRepository.findById(inventoryItemId)
                .orElseThrow(() -> new InventoryItemNotFound("Item de inventário com o [ ID: %s ] não encontrado".formatted(inventoryItemId)));
    }

    private InventoryItem validateOwnershipWithLock(UUID inventoryItemId) {
        var inventoryItem = findByIdWithLock(inventoryItemId);

        inventoryService.validateOwnershipById(inventoryItem.getInventory().getId());
        productService.validateOwnershipById(inventoryItem.getProduct().getId());

        return inventoryItem;
    }

    private InventoryItem findByIdWithLock(UUID inventoryItemId) {
        return inventoryItemRepository.findItemById(inventoryItemId)
                .orElseThrow(() -> new InventoryItemNotFound("Item de inventário com o [ ID: %s ] não encontrado".formatted(inventoryItemId)));
    }
}