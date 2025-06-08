package edu.infnet.InventorizeAPI.controllers;

import edu.infnet.InventorizeAPI.dto.request.item.ItemDTO;
import edu.infnet.InventorizeAPI.dto.request.item.PatchItemDTO;
import edu.infnet.InventorizeAPI.dto.request.item.UpdateItemDTO;
import edu.infnet.InventorizeAPI.dto.response.ItemResponseDTO;
import edu.infnet.InventorizeAPI.services.ItemService;
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
    private final ItemService itemService;

    /**
     * Cria um novo item de inventário.
     *
     * @param itemRequest dados do item a ser criado
     * @return informações do item criado
     */
    @PostMapping
    public ResponseEntity<ItemResponseDTO> createInventoryItem(@Valid @RequestBody ItemDTO itemRequest) {
        ItemResponseDTO inventoryItemInfo = itemService.create(itemRequest);

        return ResponseEntity.ok(inventoryItemInfo);
    }

    /**
     * Busca um item de inventário pelo seu ID.
     *
     * @param id identificador do item
     * @return informações do item encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> getInventoryItem(@PathVariable UUID id) {
        ItemResponseDTO inventoryItemInfo = itemService.getById(id);

        return ResponseEntity.ok(inventoryItemInfo);
    }

    /**
     * Lista todos os itens de inventário.
     *
     * @return lista de itens de inventário
     */
    @GetMapping
    public ResponseEntity<List<ItemResponseDTO>> getInventoryItems() {
        List<ItemResponseDTO> items = itemService.getAll();

        return ResponseEntity.ok(items);
    }

    /**
     * Lista todos os itens de um inventário específico.
     *
     * @param inventoryId identificador do inventário
     * @return lista de itens pertencentes ao inventário
     */
    @GetMapping("/inventory/{inventoryId}")
    public ResponseEntity<List<ItemResponseDTO>> getAllByInventory(@PathVariable UUID inventoryId) {
        List<ItemResponseDTO> items = itemService.getAllByInventoryId(inventoryId);

        return ResponseEntity.ok(items);
    }

    /**
     * Atualiza completamente um item de inventário.
     *
     * @param id identificador do item
     * @param itemRequest dados atualizados do item
     * @return informações do item atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> updateInventoryItem(@PathVariable UUID id, @Valid @RequestBody UpdateItemDTO itemRequest) {
        ItemResponseDTO updatedItem = itemService.update(id, itemRequest);

        return ResponseEntity.ok(updatedItem);
    }

    /**
     * Atualiza parcialmente um item de inventário.
     *
     * @param id identificador do item
     * @param itemRequest dados parciais para atualização
     * @return informações do item atualizado
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> patchInventoryItem(@PathVariable UUID id, @Valid @RequestBody PatchItemDTO itemRequest) {
        ItemResponseDTO patchedItem = itemService.patch(id, itemRequest);

        return ResponseEntity.ok(patchedItem);
    }

    /**
     * Atualiza a quantidade atual de um item de inventário.
     * @param id identificador do item
     * @param adjustment quantidade a ser ajustada (pode ser positiva ou negativa)
     * @return informações do item atualizado
     */
    @PatchMapping("/{id}/adjust")
    public ResponseEntity<ItemResponseDTO> adjustInventoryItemQuantity(@PathVariable UUID id, @RequestParam int adjustment) {
        ItemResponseDTO updatedItem = itemService.adjustCurrentQuantity(id, adjustment);

        return ResponseEntity.ok(updatedItem);
    }

    /**
     * Deleta um item de inventário pelo seu ID.
     *
     * @param id identificador do item
     * @return mensagem de sucesso
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInventoryItem(@PathVariable UUID id) {
        itemService.deleteById(id);

        return ResponseEntity.ok("Item deletado com sucesso");
    }
}
