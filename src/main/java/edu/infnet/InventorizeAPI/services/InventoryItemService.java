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
    private final EmailService emailService;
    private final ProductService productService;
    private final InventoryService inventoryService;
    private final InventoryItemRepository inventoryItemRepository;

    /**
     * Cria um novo item de inventário.
     *
     * @param itemRequest dados do item a ser criado
     * @return informações do item criado
     */
    public ItemResponseDTO create(ItemRequestDTO itemRequest) {
        Product product = productService.validateOwnershipById(itemRequest.productId());
        Inventory inventory = inventoryService.validateOwnershipById(itemRequest.inventoryId());

        var newInventoryItem = InventoryItem.builder()
                .product(product)
                .inventory(inventory)
                .currentQuantity(itemRequest.currentQuantity())
                .minimumStockLevel(itemRequest.minimumStockLevel())
                .build();

        var savedInventoryItem = inventoryItemRepository.save(newInventoryItem);

        return ItemResponseDTO.from(savedInventoryItem);
    }

    /**
     * Busca um item de inventário pelo seu ID.
     *
     * @param id identificador do item
     * @return informações do item encontrado
     */
    public ItemResponseDTO getById(UUID id) {
        InventoryItem inventoryItem = validateOwnershipById(id);

        return ItemResponseDTO.from(inventoryItem);
    }

    /**
     * Lista todos os itens de inventário do usuário autenticado.
     *
     * @return lista de itens de inventário
     */
    public List<ItemResponseDTO> getAll() {
        var inventoryIds = inventoryService.getAll()
                .stream()
                .map(InventoryResponseDTO::inventoryId)
                .toList();

        return inventoryItemRepository.getAllByInventoryIdIn(inventoryIds)
                .stream()
                .map(ItemResponseDTO::from)
                .toList();
    }

    /**
     * Lista todos os itens de inventário de um inventário específico.
     *
     * @param inventoryId identificador do inventário
     * @return lista de itens de inventário
     */
    public List<ItemResponseDTO> getAllByInventoryId(UUID inventoryId) {
        inventoryService.validateOwnershipById(inventoryId);

        List<InventoryItem> items = inventoryItemRepository.getAllByInventoryId(inventoryId);

        return items.stream().map(ItemResponseDTO::from).toList();
    }

    /**
     * Atualiza um item de inventário existente.
     *
     * @param id          identificador do item a ser atualizado
     * @param itemRequest dados do item a ser atualizado
     * @return informações do item atualizado
     */
    public ItemResponseDTO update(UUID id, @Valid UpdateItemDTO itemRequest) {
        InventoryItem item = validateOwnershipById(id);

        var updatedItem = item.toBuilder()
                .currentQuantity(itemRequest.currentQuantity())
                .minimumStockLevel(itemRequest.minimumStockLevel())
                .build();

        var savedItem = inventoryItemRepository.save(updatedItem);

        return ItemResponseDTO.from(savedItem);
    }

    /**
     * Atualiza parcialmente um item de inventário existente.
     *
     * @param id          identificador do item a ser atualizado
     * @param itemRequest dados do item a ser atualizado
     * @return informações do item atualizado
     */
    public ItemResponseDTO patch(UUID id, @Valid PatchItemRequestDTO itemRequest) {
        InventoryItem item = validateOwnershipById(id);

        var itemBuilder = item.toBuilder();
        if (itemRequest.currentQuantity() != null) itemBuilder.currentQuantity(itemRequest.currentQuantity());
        if (itemRequest.minimumStockLevel() != null) itemBuilder.minimumStockLevel(itemRequest.minimumStockLevel());

        itemBuilder.product(item.getProduct());
        itemBuilder.inventory(item.getInventory());

        var updatedItem = itemBuilder.build();
        var savedItem = inventoryItemRepository.save(updatedItem);

        return ItemResponseDTO.from(savedItem);
    }

    /**
     * Deleta um item de inventário pelo seu ID.
     *
     * @param id identificador do item a ser deletado
     */
    public void deleteById(UUID id) {
        InventoryItem inventoryItem = validateOwnershipById(id);
        inventoryItemRepository.delete(inventoryItem);
    }

    /**
     * Ajusta a quantidade atual de um item de inventário.
     *
     * @param itemId     identificador do item
     * @param adjustment valor a ser ajustado (positivo ou negativo)
     * @return informações do item atualizado
     */
    @Transactional
    public ItemResponseDTO adjustCurrentQuantity(UUID itemId, int adjustment) {
        var inventoryItem = validateOwnershipWithLock(itemId);

        var newQuantity = Math.max((inventoryItem.getCurrentQuantity() + adjustment), 0);
        var newItem = inventoryItem.toBuilder().currentQuantity(newQuantity).build();
        var updatedItem = inventoryItemRepository.save(newItem);

        sendEmailIfLowStock(updatedItem);

        return ItemResponseDTO.from(updatedItem);
    }

    /**
     * Valida a propriedade de um item de inventário pelo seu ID, garantindo que o usuário autenticado é o proprietário do inventário e do produto associado ao item.
     *
     * @param inventoryItemId identificador do item de inventário
     * @return InventoryItem validado
     */
    private InventoryItem validateOwnershipById(UUID inventoryItemId) {
        var inventoryItem = findById(inventoryItemId);

        inventoryService.validateOwnershipById(inventoryItem.getInventory().getId());
        productService.validateOwnershipById(inventoryItem.getProduct().getId());

        return inventoryItem;
    }

    /**
     * Busca um item de inventário pelo seu ID
     *
     * @param inventoryItemId identificador do item de inventário
     * @return InventoryItem encontrado
     */
    private InventoryItem findById(UUID inventoryItemId) {
        return inventoryItemRepository.findById(inventoryItemId)
                .orElseThrow(() -> new InventoryItemNotFound("Item de inventário com o [ ID: %s ] não encontrado".formatted(inventoryItemId)));
    }

    /**
     * Valida a propriedade de um item de inventário pelo seu ID, garantindo que o usuário autenticado é o proprietário do inventário e do produto associado ao item, com bloqueio para evitar concorrência.
     *
     * @param inventoryItemId identificador do item de inventário
     * @return InventoryItem validado com bloqueio
     */
    private InventoryItem validateOwnershipWithLock(UUID inventoryItemId) {
        var inventoryItem = findByIdWithLock(inventoryItemId);

        inventoryService.validateOwnershipById(inventoryItem.getInventory().getId());
        productService.validateOwnershipById(inventoryItem.getProduct().getId());

        return inventoryItem;
    }

    /**
     * Busca um item de inventário pelo seu ID com bloqueio para evitar concorrência.
     *
     * @param inventoryItemId identificador do item de inventário
     * @return InventoryItem encontrado com bloqueio
     * @throws InventoryItemNotFound se o item não for encontrado
     */
    private InventoryItem findByIdWithLock(UUID inventoryItemId) {
        return inventoryItemRepository.findItemById(inventoryItemId)
                .orElseThrow(() -> new InventoryItemNotFound("Item de inventário com o [ ID: %s ] não encontrado".formatted(inventoryItemId)));
    }

    /**
    * Verifica se o estoque do item está baixo e envia um e-mail de notificação se necessário.
    *
    * @param item o item de inventário a ser verificado
    */
    private void sendEmailIfLowStock(InventoryItem item) {
        if (isLowStock(item)) {
            String emailBody = emailService.createEmailBody(item.getInventory().getName(), item.getProduct().getName(), item.getCurrentQuantity());
            emailService.sendEmail(item.getInventory().getNotificationEmail(), emailBody);
        }
    }

    /**
     * Verifica se o item de inventário está com estoque baixo.
     *
     * @param item o item de inventário a ser verificado
     * @return true se o estoque estiver baixo, false caso contrário
     */
    private boolean isLowStock(InventoryItem item) {
        return item.getCurrentQuantity() <= item.getMinimumStockLevel();
    }
}