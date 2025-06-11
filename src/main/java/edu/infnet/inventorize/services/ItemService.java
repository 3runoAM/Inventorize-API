package edu.infnet.inventorize.services;

import edu.infnet.inventorize.dto.request.item.ItemDTO;
import edu.infnet.inventorize.dto.request.item.PatchItemDTO;
import edu.infnet.inventorize.dto.request.item.UpdateItemDTO;
import edu.infnet.inventorize.dto.response.InventoryResponseDTO;
import edu.infnet.inventorize.dto.response.ItemResponseDTO;
import edu.infnet.inventorize.entities.*;
import edu.infnet.inventorize.exceptions.custom.InsufficientStockException;
import edu.infnet.inventorize.exceptions.custom.InventoryItemNotFound;
import edu.infnet.inventorize.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ItemService {
    private final EmailService emailService;
    private final ProductService productService;
    private final InventoryService inventoryService;
    private final ItemRepository itemRepository;

    /**
     * Cria um novo item de inventário.
     *
     * @param itemRequest dados do item a ser criado
     * @return informações do item criado
     */
    public ItemResponseDTO create(ItemDTO itemRequest) {
        Product product = productService.validateOwnershipById(itemRequest.productId());
        Inventory inventory = inventoryService.validateOwnershipById(itemRequest.inventoryId());

        var newInventoryItem = Item.builder()
                .product(product)
                .inventory(inventory)
                .currentQuantity(itemRequest.currentQuantity())
                .minimumStockLevel(itemRequest.minimumStockLevel())
                .build();

        var savedInventoryItem = itemRepository.save(newInventoryItem);

        return ItemResponseDTO.from(savedInventoryItem);
    }

    /**
     * Busca um item de inventário pelo seu ID.
     *
     * @param id identificador do item
     * @return informações do item encontrado
     */
    public ItemResponseDTO getById(UUID id) {
        Item item = validateOwnershipById(id);

        return ItemResponseDTO.from(item);
    }

    /**
     * Lista todos os itens de inventário do usuário autenticado.
     *
     * @return lista de itens de inventário
     */
    public List<ItemResponseDTO> getAll() {
        var inventoryIds = inventoryService.getAll()
                .stream()
                .map(InventoryResponseDTO::id)
                .toList();

        return itemRepository.getAllItemsByInventoryIdIn(inventoryIds)
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
    public List<ItemResponseDTO> getAllItemsByInventoryId(UUID inventoryId) {
        inventoryService.validateOwnershipById(inventoryId);

        List<Item> items = itemRepository.getAllItemsByInventoryId(inventoryId);

        return items.stream()
                .map(ItemResponseDTO::from)
                .toList();
    }

    /**
     * Atualiza um item de inventário existente.
     *
     * @param id          identificador do item a ser atualizado
     * @param itemRequest dados do item a ser atualizado
     * @return informações do item atualizado
     */
    public ItemResponseDTO update(UUID id, UpdateItemDTO itemRequest) {
        Item item = validateOwnershipById(id);

        var updatedItem = item.toBuilder()
                .currentQuantity(itemRequest.currentQuantity())
                .minimumStockLevel(itemRequest.minimumStockLevel())
                .build();

        var savedItem = itemRepository.save(updatedItem);

        return ItemResponseDTO.from(savedItem);
    }

    /**
     * Atualiza parcialmente um item de inventário existente.
     *
     * @param id          identificador do item a ser atualizado
     * @param itemRequest dados do item a ser atualizado
     * @return informações do item atualizado
     */
    public ItemResponseDTO patch(UUID id, PatchItemDTO itemRequest) {
        Item item = validateOwnershipById(id);

        var itemBuilder = item.toBuilder();
        if (itemRequest.currentQuantity() != null) itemBuilder.currentQuantity(itemRequest.currentQuantity());
        if (itemRequest.minimumStockLevel() != null) itemBuilder.minimumStockLevel(itemRequest.minimumStockLevel());

        var updatedItem = itemBuilder.build();
        var savedItem = itemRepository.save(updatedItem);

        return ItemResponseDTO.from(savedItem);
    }

    /**
     * Deleta um item de inventário pelo seu ID.
     *
     * @param id identificador do item a ser deletado
     */
    public void deleteById(UUID id) {
        Item item = validateOwnershipById(id);
        itemRepository.delete(item);
    }

    /**
     * Ajusta a quantidade atual de um item de inventário.
     *
     * @param itemId     identificador do item
     * @param adjustment valor a ser ajustado (positivo ou negativo)
     * @return informações do item atualizado
     * @throws InsufficientStockException se o ajuste resultar em quantidade negativa
     */
    @Transactional
    public ItemResponseDTO adjustCurrentQuantity(UUID itemId, int adjustment) {
        var item = validateOwnershipWithLock(itemId);

        int newQuantity = item.getCurrentQuantity() + adjustment;

        if (newQuantity < 0) {
            throw new InsufficientStockException(String.format("Ajuste de estoque não pode resultar em quantidade negativa.\nITEM: [ %s ] \nEM ESTOQUE: %d ", item.getProduct().getName(), item.getCurrentQuantity()));
        }

        var newItem = item.toBuilder()
                .currentQuantity(newQuantity)
                .build();

        var updatedItem = itemRepository.save(newItem);

        sendEmailIfLowStock(updatedItem);

        return ItemResponseDTO.from(updatedItem);
    }

    public List<ItemResponseDTO> getLowStockItems() {
        var inventoryIds = inventoryService.getAll()
                .stream()
                .map(InventoryResponseDTO::id)
                .toList();

        List<Item> lowStockItems = itemRepository.getAllWhereMinimumStockLevelIsLowerThanCurrentQuantityByInventoryIdIn(inventoryIds);

        return lowStockItems.stream()
                .map(ItemResponseDTO::from)
                .toList();
    }

    /**
     * Valida a propriedade de um item de inventário pelo seu ID, garantindo que o usuário autenticado é o proprietário do inventário e do produto associado ao item.
     *
     * @param inventoryItemId identificador do item de inventário
     * @return InventoryItem validado
     */
    protected Item validateOwnershipById(UUID inventoryItemId) {
        var inventoryItem = itemRepository.findById(inventoryItemId)
                .orElseThrow(() -> new InventoryItemNotFound("Item de inventário com o [ ID: %s ] não encontrado".formatted(inventoryItemId)));

        inventoryService.validateOwnershipById(inventoryItem.getInventory().getId());
        productService.validateOwnershipById(inventoryItem.getProduct().getId());

        return inventoryItem;
    }

    /**
     * Valida a propriedade de um item de inventário pelo seu ID, garantindo que o usuário autenticado é o proprietário do inventário e do produto associado ao item, com bloqueio para evitar concorrência.
     *
     * @param inventoryItemId identificador do item de inventário
     * @return InventoryItem validado com bloqueio
     */
    protected Item validateOwnershipWithLock(UUID inventoryItemId) {
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
    private Item findByIdWithLock(UUID inventoryItemId) {
        return itemRepository.findItemById(inventoryItemId)
                .orElseThrow(() -> new InventoryItemNotFound("Item de inventário com o [ ID: %s ] não encontrado".formatted(inventoryItemId)));
    }

    /**
     * Verifica se o estoque do item está baixo e envia um e-mail de notificação se necessário.
     *
     * @param item o item de inventário a ser verificado
     */
    private void sendEmailIfLowStock(Item item) {
        if (isLowStock(item)) {
            String emailBody = emailService.createEmailBody(
                    item.getInventory().getName(),
                    item.getProduct().getName(),
                    item.getCurrentQuantity()
            );

            emailService.sendEmail(item.getInventory().getNotificationEmail(), emailBody);
        }
    }

    /**
     * Verifica se o item de inventário está com estoque baixo.
     *
     * @param item o item de inventário a ser verificado
     * @return true se o estoque estiver baixo, false caso contrário
     */
    private boolean isLowStock(Item item) {
        return item.getCurrentQuantity() <= item.getMinimumStockLevel();
    }
}