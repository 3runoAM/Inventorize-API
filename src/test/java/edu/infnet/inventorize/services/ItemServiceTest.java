package edu.infnet.inventorize.services;

import edu.infnet.inventorize.dto.request.item.ItemDTO;
import edu.infnet.inventorize.dto.request.item.PatchItemDTO;
import edu.infnet.inventorize.dto.request.item.UpdateItemDTO;
import edu.infnet.inventorize.dto.response.InventoryResponseDTO;
import edu.infnet.inventorize.entities.AuthUser;
import edu.infnet.inventorize.entities.Inventory;
import edu.infnet.inventorize.entities.Item;
import edu.infnet.inventorize.entities.Product;
import edu.infnet.inventorize.enums.Role;
import edu.infnet.inventorize.exceptions.custom.InsufficientStockException;
import edu.infnet.inventorize.exceptions.custom.InventoryItemNotFound;
import edu.infnet.inventorize.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private EmailService emailService;

    @Mock
    private ProductService productService;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    // TESTES DE CRIAÇÃO ------------------------------------------------------------------------------------------------
    @Test
    public void shouldCreateItemCorrectly() {
        var itemRequestDto = getItemDTO();
        var product = createProduct();
        var inventory = createInventory();
        var item = createItem();
        var itemCaptor = getItemArgumentCaptor();

        when(productService.validateOwnershipById(itemRequestDto.productId())).thenReturn(product);
        when(inventoryService.validateOwnershipById(itemRequestDto.inventoryId())).thenReturn(inventory);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        var itemResponseDTO = itemService.create(itemRequestDto);

        verify(itemRepository).save(itemCaptor.capture());
        var savedItem = itemCaptor.getValue();

        assertEquals(item.getId(), itemResponseDTO.id(), "ID do item retornado deve ser igual ao do item salvo");

        assertEquals(item.getProduct().getId(), savedItem.getProduct().getId(), "O ID do produto associado deve ser igual ao produto mockado");
        assertEquals(item.getInventory().getId(), savedItem.getInventory().getId(), "O ID do inventário associado deve ser igual ao inventário mockado");
        assertEquals(item.getCurrentQuantity(), savedItem.getCurrentQuantity(), "A quantidade atual do item deve ser igual à quantidade mockada");
        assertEquals(item.getMinimumStockLevel(), savedItem.getMinimumStockLevel(), "A quantidade mínima em estoque do item deve ser igual ao nível mockado");
    }

    @Test
    public void shouldCallCorrectMethodsWhenCreatingItem() {
        var itemRequestDto = getItemDTO();
        var product = createProduct();
        var inventory = createInventory();
        var item = createItem();

        when(productService.validateOwnershipById(itemRequestDto.productId())).thenReturn(product);
        when(inventoryService.validateOwnershipById(itemRequestDto.inventoryId())).thenReturn(inventory);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        itemService.create(itemRequestDto);

        verify(productService, times(1)).validateOwnershipById(itemRequestDto.productId());
        verify(inventoryService, times(1)).validateOwnershipById(itemRequestDto.inventoryId());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    // TESTES DE BUSCA ---------------------------------------------------------------------------------------------------
    @Test
    public void shouldGetItemByIdCorrectly() {
        var inventory = createInventory();
        var product = createProduct();
        var item = createItem();

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(inventoryService.validateOwnershipById(item.getInventory().getId())).thenReturn(inventory);
        when(productService.validateOwnershipById(item.getProduct().getId())).thenReturn(product);

        var itemResponseDTO = itemService.getById(item.getId());

        assertEquals(item.getId(), itemResponseDTO.id(), "O ID do item retornado deve ser igual ao ID do item buscado");

        assertEquals(item.getProduct().getId(), itemResponseDTO.productId(), "O ID do produto associado deve ser igual ao do produto do item");
        assertEquals(item.getInventory().getId(), itemResponseDTO.inventoryId(), "O ID do inventário associado deve ser igual ao do inventário do item");
        assertEquals(item.getCurrentQuantity(), itemResponseDTO.currentQuantity(), "A quantidade atual do item deve ser igual à quantidade do item");
        assertEquals(item.getMinimumStockLevel(), itemResponseDTO.minimumStockLevel(), "A quantidade mínima em estoque do item deve ser igual ao nível do item");
    }

    @Test
    public void shouldCallCorrectMethodsWhenGettingItemById() {
        var inventory = createInventory();
        var product = createProduct();
        var item = createItem();

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(inventoryService.validateOwnershipById(item.getInventory().getId())).thenReturn(inventory);
        when(productService.validateOwnershipById(item.getProduct().getId())).thenReturn(product);

        itemService.getById(item.getId());

        verify(itemRepository, times(1)).findById(item.getId());
        verify(inventoryService, times(1)).validateOwnershipById(item.getInventory().getId());
        verify(productService, times(1)).validateOwnershipById(item.getProduct().getId());
    }

    @Test
    public void shouldGetAllItemsCorrectly() {
        var inventoryResponseDtoList = getInventoryResponseDTOList();
        var inventoryIdsList = getInventoryIdsList();
        var itemList = createItemListWithDifferentInventories();
        var user = createAuthUser();

        when(inventoryService.getAll()).thenReturn(inventoryResponseDtoList);
        when(itemRepository.getAllItemsByInventoryIdIn(inventoryIdsList)).thenReturn(itemList);

        var itemDtoList = itemService.getAll();

        assertEquals(itemList.size(), itemDtoList.size());

        for (var i = 0; i < itemList.size(); i++) {
            var item = itemList.get(i);
            var responseDto = itemDtoList.get(i);

            assertEquals(item.getId(), responseDto.id(), "O ID do item deve ser igual ao ID do DTO");

            assertEquals(item.getProduct().getId(), responseDto.productId(), "O id do produto associado ser igual ao ID do DTO");
            assertEquals(item.getInventory().getId(), responseDto.inventoryId(), "O id do inventário associado deve ser igual ao ID do DTO");
            assertEquals(item.getCurrentQuantity(), responseDto.currentQuantity(), "A quantidade atual deve ser igual à do DTO");
            assertEquals(item.getMinimumStockLevel(), responseDto.minimumStockLevel(), "A quantidade mínima em estoque deve ser igual ao do DTO");
        }
    }

    @Test
    public void shouldCallCorrectMethodsWhenGettingAllItems() {
        var inventoryResponseDtoList = getInventoryResponseDTOList();
        var inventoryIdsList = getInventoryIdsList();
        var itemList = createItemListWithDifferentInventories();

        when(inventoryService.getAll()).thenReturn(inventoryResponseDtoList);
        when(itemRepository.getAllItemsByInventoryIdIn(inventoryIdsList)).thenReturn(itemList);

        itemService.getAll();

        verify(inventoryService, times(1)).getAll();
        verify(itemRepository, times(1)).getAllItemsByInventoryIdIn(inventoryIdsList);
    }

    @Test
    public void shouldGetAllItemsByInventoryIdCorrectly() {
        var inventory = createInventory();
        var itemList = createItemListForTheSameInventory();

        when(inventoryService.validateOwnershipById(inventory.getId())).thenReturn(inventory);
        when(itemRepository.getAllItemsByInventoryId(inventory.getId())).thenReturn(itemList);

        var itemResponseDtoList = itemService.getAllItemsByInventoryId(inventory.getId());

        assertEquals(itemList.size(), itemResponseDtoList.size(), "O número de itens retornados deve ser igual ao número de itens no inventário");
        for (var i = 0; i < itemList.size(); i++) {
            var itemResponseDto = itemResponseDtoList.get(i);
            var itemEntity = itemList.get(i);

            assertEquals(itemEntity.getId(), itemResponseDto.id(), "O ID do item deve ser igual ao ID do DTO");
            assertEquals(itemEntity.getProduct().getId(), itemResponseDto.productId(), "O ID do produto associado deve ser igual ao ID do DTO");
            assertEquals(itemEntity.getInventory().getId(), itemResponseDto.inventoryId(), "O ID do inventário associado deve ser igual ao ID do DTO");
            assertEquals(itemEntity.getCurrentQuantity(), itemResponseDto.currentQuantity(), "A quantidade atual do item deve ser igual à do DTO");
            assertEquals(itemEntity.getMinimumStockLevel(), itemResponseDto.minimumStockLevel(), "A quantidade mínima em estoque do item deve ser igual ao do DTO");
        }
    }

    @Test
    public void shouldCallCorrectMethodsWhenGettingAllItemsByInventoryId() {
        var inventory = createInventory();
        var itemList = createItemListForTheSameInventory();

        when(inventoryService.validateOwnershipById(inventory.getId())).thenReturn(inventory);
        when(itemRepository.getAllItemsByInventoryId(inventory.getId())).thenReturn(itemList);

        itemService.getAllItemsByInventoryId(inventory.getId());

        verify(inventoryService, times(1)).validateOwnershipById(inventory.getId());
        verify(itemRepository, times(1)).getAllItemsByInventoryId(inventory.getId());
        verifyNoMoreInteractions(itemRepository, inventoryService, productService);
    }

    // TESTES DE ATUALIZAÇÃO -------------------------------------------------------------------------------------------
    @Test
    public void shouldUpdateCorrectly() {
        var item = createItem();
        var updateItemDTO = new UpdateItemDTO(15, 3);
        var inventory = createInventory();
        var product = createProduct();
        var updatedItem = item.toBuilder()
                .currentQuantity(updateItemDTO.currentQuantity())
                .minimumStockLevel(updateItemDTO.minimumStockLevel())
                .build();
        var itemCaptor = getItemArgumentCaptor();

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(inventoryService.validateOwnershipById(item.getInventory().getId())).thenReturn(inventory);
        when(productService.validateOwnershipById(item.getProduct().getId())).thenReturn(product);
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        var itemResponseDTO = itemService.update(item.getId(), updateItemDTO);
        verify(itemRepository).save(itemCaptor.capture());
        var savedItem = itemCaptor.getValue();

        assertEquals(item.getId(), itemResponseDTO.id(), "O ID do item retornado deve ser igual ao ID do item salvo");

        assertEquals(product.getId(), savedItem.getProduct().getId(), "O ID do produto associado deve ser igual ao do produto mockado");
        assertEquals(inventory.getId(), savedItem.getInventory().getId(), "O ID do inventário associado deve ser igual ao do inventário mockado");
        assertEquals(updateItemDTO.currentQuantity(), savedItem.getCurrentQuantity(), "A quantidade atual do item deve ser igual à quantidade da requisição");
        assertEquals(updateItemDTO.minimumStockLevel(), savedItem.getMinimumStockLevel(), "O limite de estoque do item deve ser igual ao nível da requisição");
    }

    @Test
    public void shouldCallCorrectMethodsWhenUpdating() {
        var item = createItem();
        var updateItemDTO = new UpdateItemDTO(15, 3);
        var inventory = createInventory();
        var product = createProduct();
        var itemCaptor = getItemArgumentCaptor();

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(inventoryService.validateOwnershipById(item.getInventory().getId())).thenReturn(inventory);
        when(productService.validateOwnershipById(item.getProduct().getId())).thenReturn(product);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        var itemResponseDTO = itemService.update(item.getId(), updateItemDTO);

        verify(itemRepository, times(1)).findById(item.getId());
        verify(inventoryService, times(1)).validateOwnershipById(item.getInventory().getId());
        verify(productService, times(1)).validateOwnershipById(item.getProduct().getId());
        verify(itemRepository, times(1)).save(itemCaptor.capture());
        verifyNoMoreInteractions(itemRepository, inventoryService, productService);
    }

    @Test
    public void shouldPatchItemCorrectlyWithFullData() {
        var inventory = createInventory();
        var product = createProduct();

        var patchItemDTO = new PatchItemDTO(20, 10);
        var item = createItem();
        var updatedItem = item.toBuilder()
                .currentQuantity(patchItemDTO.currentQuantity())
                .minimumStockLevel(patchItemDTO.minimumStockLevel())
                .build();
        var itemCaptor = getItemArgumentCaptor();

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(inventoryService.validateOwnershipById(item.getInventory().getId())).thenReturn(inventory);
        when(productService.validateOwnershipById(item.getProduct().getId())).thenReturn(product);
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        var itemResponseDto = itemService.patch(item.getId(), patchItemDTO);
        verify(itemRepository).save(itemCaptor.capture());
        var savedItem = itemCaptor.getValue();

        assertEquals(item.getId(), itemResponseDto.id(), "O ID do item retornado deve ser igual ao ID do item salvo");

        assertEquals(product.getId(), savedItem.getProduct().getId(), "O produto associado não deve ser alterado");
        assertEquals(inventory.getId(), savedItem.getInventory().getId(), "O inventário associado não deve ser alterado");
        assertEquals(updatedItem.getCurrentQuantity(), savedItem.getCurrentQuantity(), "A quantidade atual do item deve ser igual à quantidade da requisição");
        assertEquals(updatedItem.getMinimumStockLevel(), savedItem.getMinimumStockLevel(), "A quantidade mínima em estoque do item deve ser igual ao nível da requisição");
    }

    @Test
    public void shouldPatchItemCorrectlyWithPartialData() {
        var inventory = createInventory();
        var product = createProduct();

        var patchItemDTO = new PatchItemDTO(null, 1);
        var item = createItem();
        var updatedItem = item.toBuilder()
                .minimumStockLevel(patchItemDTO.minimumStockLevel())
                .build();
        var itemCaptor = getItemArgumentCaptor();

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(inventoryService.validateOwnershipById(item.getInventory().getId())).thenReturn(inventory);
        when(productService.validateOwnershipById(item.getProduct().getId())).thenReturn(product);
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        var itemResponseDto = itemService.patch(item.getId(), patchItemDTO);
        verify(itemRepository).save(itemCaptor.capture());
        var savedItem = itemCaptor.getValue();

        assertEquals(item.getId(), itemResponseDto.id(), "O ID do item retornado deve ser igual ao ID do item salvo");

        assertEquals(product.getId(), savedItem.getProduct().getId(), "O produto associado não deve ser alterado");
        assertEquals(inventory.getId(), savedItem.getInventory().getId(), "O inventário associado não deve ser alterado");
        assertEquals(item.getCurrentQuantity(), savedItem.getCurrentQuantity(), "A quantidade atual do item não deve ser alterada");
        assertEquals(patchItemDTO.minimumStockLevel(), savedItem.getMinimumStockLevel(), "A quantidade mínima em estoque do item deve ser igual ao nível da requisição");
    }

    @Test
    public void shouldCallCorrectMethodsWhenPatchingItem() {
        var inventory = createInventory();
        var product = createProduct();

        var patchItemDTO = new PatchItemDTO(20, 10);
        var item = createItem();
        var updatedItem = item.toBuilder()
                .currentQuantity(patchItemDTO.currentQuantity())
                .minimumStockLevel(patchItemDTO.minimumStockLevel())
                .build();

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(inventoryService.validateOwnershipById(item.getInventory().getId())).thenReturn(inventory);
        when(productService.validateOwnershipById(item.getProduct().getId())).thenReturn(product);
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        itemService.patch(item.getId(), patchItemDTO);

        verify(itemRepository, times(1)).findById(item.getId());
        verify(inventoryService, times(1)).validateOwnershipById(item.getInventory().getId());
        verify(productService, times(1)).validateOwnershipById(item.getProduct().getId());
        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoMoreInteractions(inventoryService, productService, itemRepository);
    }

    @Test
    public void shouldNotPatchItem() {
        var inventory = createInventory();
        var product = createProduct();
        var patchItemDTO = new PatchItemDTO(null, null);
        var item = createItem();
        var itemCaptor = getItemArgumentCaptor();

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(inventoryService.validateOwnershipById(item.getInventory().getId())).thenReturn(inventory);
        when(productService.validateOwnershipById(item.getProduct().getId())).thenReturn(product);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        var itemResponseDto = itemService.patch(item.getId(), patchItemDTO);
        verify(itemRepository).save(itemCaptor.capture());
        var savedItem = itemCaptor.getValue();

        assertEquals(item.getId(), itemResponseDto.id(), "O ID do item retornado deve ser igual ao ID do item salvo");

        assertEquals(product.getId(), savedItem.getProduct().getId(), "O produto associado não deve ser alterado");
        assertEquals(inventory.getId(), savedItem.getInventory().getId(), "O inventário associado não deve ser alterado");
        assertEquals(item.getCurrentQuantity(), savedItem.getCurrentQuantity(), "A quantidade atual do item não deve ser alterada");
        assertEquals(item.getMinimumStockLevel(), savedItem.getMinimumStockLevel(), "A quantidade mínima em estoque do item não deve ser alterado");
    }

    // TESTES DE AJUSTE DE QUANTIDADE ----------------------------------------------------------------------------------
    @Test
    public void shouldAdjustQuantityUpCorrectly() {
        var inventory = createInventory();
        var product = createProduct();
        var item = createItem();

        var adjustment = 5;
        var newQuantity = item.getCurrentQuantity() + adjustment;

        var updatedItem = item.toBuilder()
                .currentQuantity(newQuantity)
                .build();

        var itemCaptor = getItemArgumentCaptor();

        when(itemRepository.findItemById(item.getId())).thenReturn(Optional.of(item));
        when(inventoryService.validateOwnershipById(item.getInventory().getId())).thenReturn(inventory);
        when(productService.validateOwnershipById(item.getProduct().getId())).thenReturn(product);
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        var itemResponseDto = itemService.adjustCurrentQuantity(item.getId(), adjustment);
        verify(itemRepository).save(itemCaptor.capture());
        var savedItem = itemCaptor.getValue();

        assertEquals(item.getId(), itemResponseDto.id(), "O ID do item retornado deve ser igual ao ID do item salvo");
        assertEquals(product.getId(), savedItem.getProduct().getId(), "O produto associado não deve ser alterado");
        assertEquals(inventory.getId(), savedItem.getInventory().getId(), "O inventário associado não deve ser alterado");
        assertEquals(newQuantity, savedItem.getCurrentQuantity(), "A quantidade atual do item deve ser atualizada corretamente em +5");
        assertEquals(item.getMinimumStockLevel(), savedItem.getMinimumStockLevel(), "A quantidade mínima em estoque do item não deve ser alterada");
    }

    @Test
    public void shouldAdjustQuantityDownCorrectly() {
        var inventory = createInventory();
        var product = createProduct();
        var item = createItem();

        var adjustment = -5;
        var newQuantity = item.getCurrentQuantity() + adjustment;

        var updatedItem = item.toBuilder()
                .currentQuantity(newQuantity)
                .build();

        var itemCaptor = getItemArgumentCaptor();

        when(itemRepository.findItemById(item.getId())).thenReturn(Optional.of(item));
        when(inventoryService.validateOwnershipById(inventory.getId())).thenReturn(inventory);
        when(productService.validateOwnershipById(product.getId())).thenReturn(product);
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        var itemResponseDto = itemService.adjustCurrentQuantity(item.getId(), adjustment);
        verify(itemRepository).save(itemCaptor.capture());
        var savedItem = itemCaptor.getValue();

        assertEquals(item.getId(), itemResponseDto.id(), "O ID do item retornado deve ser igual ao ID do item salvo");
        assertEquals(product.getId(), savedItem.getProduct().getId(), "O produto associado não deve ser alterado");
        assertEquals(inventory.getId(), savedItem.getInventory().getId(), "O inventário associado não deve ser alterado");
        assertEquals(newQuantity, savedItem.getCurrentQuantity(), "A quantidade atual do item deve ser atualizada corretamente em -5");
        assertEquals(item.getMinimumStockLevel(), savedItem.getMinimumStockLevel(), "A quantidade mínima em estoque do item não deve ser alterada");
    }

    @Test
    public void shouldThrowExceptionWhenInsufficientStock() {
        var inventory = createInventory();
        var product = createProduct();
        var item = createItem();

        var adjustment = -50;
        var newQuantity = item.getCurrentQuantity() + adjustment;

        when(itemRepository.findItemById(item.getId())).thenReturn(Optional.of(item));
        when(inventoryService.validateOwnershipById(inventory.getId())).thenReturn(inventory);
        when(productService.validateOwnershipById(product.getId())).thenReturn(product);

        var insufficientStockException = assertThrows(InsufficientStockException.class,
                () -> itemService.adjustCurrentQuantity(item.getId(), adjustment),
                "Ajuste de estoque não pode resultar em quantidade negativa");
        assertEquals(String.format("Ajuste de estoque não pode resultar em quantidade negativa.\nITEM: [ %s ] \nEM ESTOQUE: %d ", item.getProduct().getName(), item.getCurrentQuantity()),
                insufficientStockException.getMessage(),
                "A mensagem de exceção deve exibir o nome do item e a quantidade atual");
    }

    // TESTE DE ENVIO DE EMAIL -----------------------------------------------------------------------------------------
    @Test
    public void shouldSendEmailAfterAdjustingDown() {
        var inventory = createInventory();
        var product = createProduct();
        var item = createItem();
        var itemCaptor = getItemArgumentCaptor();
        var emailBody = String.format("O inventário '%s' está com o item '%s' com quantidade baixa: %d unidades.",
                inventory.getName(), item.getProduct().getName(), item.getCurrentQuantity());

        var adjustment = -8;
        var newQuantity = item.getCurrentQuantity() + adjustment;

        var updatedItem = item.toBuilder()
                .currentQuantity(newQuantity)
                .build();

        when(itemRepository.findItemById(item.getId())).thenReturn(Optional.of(item));
        when(inventoryService.validateOwnershipById(inventory.getId())).thenReturn(inventory);
        when(productService.validateOwnershipById(product.getId())).thenReturn(product);
        when(emailService.createEmailBody(inventory.getName(), item.getProduct().getName(), newQuantity)).thenReturn(emailBody);
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        itemService.adjustCurrentQuantity(item.getId(), adjustment);
        verify(itemRepository).save(itemCaptor.capture());
        var savedItem = itemCaptor.getValue();



        verify(emailService, times(1)).createEmailBody(inventory.getName(), item.getProduct().getName(), newQuantity);
        verify(emailService, times(1)).sendEmail(inventory.getNotificationEmail(), emailBody);
    }

    @Test
    public void shouldNotSendEmailAfterAdjustingDown() {
        var inventory = createInventory();
        var product = createProduct();
        var item = createItem();

        var adjustment = -2;
        var newQuantity = item.getCurrentQuantity() + adjustment;

        var updatedItem = item.toBuilder()
                .currentQuantity(newQuantity)
                .build();

        when(itemRepository.findItemById(item.getId())).thenReturn(Optional.of(item));
        when(inventoryService.validateOwnershipById(inventory.getId())).thenReturn(inventory);
        when(productService.validateOwnershipById(product.getId())).thenReturn(product);
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        itemService.adjustCurrentQuantity(item.getId(), adjustment);

        verifyNoInteractions(emailService);
    }

    // TESTE DE DELEÇÃO ------------------------------------------------------------------------------------------------
    @Test
    public void shouldDeleteItemById() {
        var inventory = createInventory();
        var product = createProduct();
        var item = createItem();

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(inventoryService.validateOwnershipById(item.getInventory().getId())).thenReturn(inventory);
        when(productService.validateOwnershipById(item.getProduct().getId())).thenReturn(product);

        itemService.deleteById(item.getId());

        verify(itemRepository, times(1)).findById(item.getId());
        verify(inventoryService, times(1)).validateOwnershipById(item.getInventory().getId());
        verify(productService, times(1)).validateOwnershipById(item.getProduct().getId());
        verify(itemRepository, times(1)).delete(item);
    }

    // TESTES DE VALIDAÇÃO DE PROPRIEDADE -------------------------------------------------------------------------------
    @Test
    public void shouldValidateItemOwnershipById() {
        var inventory = createInventory();
        var product = createProduct();
        var item = createItem();

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(inventoryService.validateOwnershipById(item.getInventory().getId())).thenReturn(inventory);
        when(productService.validateOwnershipById(item.getProduct().getId())).thenReturn(product);

        var validatedItem = itemService.validateOwnershipById(item.getId());

        assertEquals(item.getProduct().getOwner(),
                validatedItem.getProduct().getOwner(),
                "O proprietário do item deve ser o mesmo do produto");
        assertEquals(item.getInventory().getOwner(),
                validatedItem.getInventory().getOwner(),
                "O proprietário do item deve ser o mesmo do inventário");
    }

    @Test
    public void shouldThrowExceptionWhenItemNotFound() {
        var itemId = UUID.randomUUID();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        var inventoryItemNotFound = assertThrows(InventoryItemNotFound.class,
                () -> itemService.validateOwnershipById(itemId),
                "Deve lançar uma exceção quando o item não for encontrado");

        assertEquals(String.format("Item de inventário com o [ ID: %s ] não encontrado", itemId), inventoryItemNotFound.getMessage());
    }

    @Test
    public void shouldValidateItemOwnershipWithLock() {
        var inventory = createInventory();
        var product = createProduct();
        var item = createItem();

        when(itemRepository.findItemById(item.getId())).thenReturn(Optional.of(item));
        when(inventoryService.validateOwnershipById(item.getInventory().getId())).thenReturn(inventory);
        when(productService.validateOwnershipById(item.getProduct().getId())).thenReturn(product);

        var validatedItem = itemService.validateOwnershipWithLock(item.getId());

        assertEquals(item.getProduct().getOwner(),
                validatedItem.getProduct().getOwner(),
                "O proprietário do item deve ser o mesmo do produto");
        assertEquals(item.getInventory().getOwner(),
                validatedItem.getInventory().getOwner(),
                "O proprietário do item deve ser o mesmo do inventário");
    }

    @Test
    public void shouldThrowExceptionWhenItemNotFoundWithLock() {
        var itemId = UUID.randomUUID();

        when(itemRepository.findItemById(itemId)).thenReturn(Optional.empty());

        var inventoryItemNotFound = assertThrows(InventoryItemNotFound.class,
                () -> itemService.validateOwnershipWithLock(itemId),
                "Deve lançar uma exceção quando o item não for encontrado com bloqueio");

        assertEquals(String.format("Item de inventário com o [ ID: %s ] não encontrado", itemId), inventoryItemNotFound.getMessage());
    }

    // TESTES DE RECUPERAÇÃO DE ITENS COM ESTOQUE BAIXO ----------------------------------------------------------------
    @Test
    public void shouldGetLowStockItemsCorrectly() {
        var inventoryResponseDtoList = getInventoryResponseDTOList();
        var inventoryIdsList = getInventoryIdsList();
        var lowStockItems = createLowStockItemList();

        when(inventoryService.getAll()).thenReturn(inventoryResponseDtoList);
        when(itemRepository.getAllWhereMinimumStockLevelIsLowerThanCurrentQuantityByInventoryIdIn(inventoryIdsList)).thenReturn(lowStockItems);

        var lowStockItemResponseDtoList = itemService.getLowStockItems();

        assertEquals(lowStockItems.size(), lowStockItemResponseDtoList.size(), "O número de itens de baixo estoque deve ser igual ao número de itens retornados");
        for (int i = 0; i < lowStockItems.size(); i++) {
            var item = lowStockItems.get(i);
            var responseDto = lowStockItemResponseDtoList.get(i);

            assertEquals(item.getId(), responseDto.id(), "O ID do item deve ser igual ao ID do DTO");
            assertEquals(item.getProduct().getId(), responseDto.productId(), "O ID do produto associado deve ser igual ao ID do DTO");
            assertEquals(item.getInventory().getId(), responseDto.inventoryId(), "O ID do inventário associado deve ser igual ao ID do DTO");
            assertEquals(item.getCurrentQuantity(), responseDto.currentQuantity(), "A quantidade atual deve ser igual à do DTO");
            assertEquals(item.getMinimumStockLevel(), responseDto.minimumStockLevel(), "A quantidade mínima em estoque deve ser igual ao do DTO");
        }
    }

    @Test
    public void shouldCallCorrectMethodsWhenGettingLowStockItems() {
        var inventoryResponseDtoList = getInventoryResponseDTOList();
        var inventoryIdsList = getInventoryIdsList();
        var lowStockItems = createLowStockItemList();

        when(inventoryService.getAll()).thenReturn(inventoryResponseDtoList);
        when(itemRepository.getAllWhereMinimumStockLevelIsLowerThanCurrentQuantityByInventoryIdIn(inventoryIdsList)).thenReturn(lowStockItems);

        itemService.getLowStockItems();

        verify(inventoryService, times(1)).getAll();
        verify(itemRepository, times(1)).getAllWhereMinimumStockLevelIsLowerThanCurrentQuantityByInventoryIdIn(inventoryIdsList);
        verifyNoMoreInteractions(inventoryService, itemRepository);
    }

    // MÉTODOS UTILITÁRIOS ---------------------------------------------------------------------------------------------
    private ItemDTO getItemDTO() {
        return new ItemDTO(
                UUID.fromString("cc60a7df-1ddd-47c6-bcc1-765f530bed6c"),
                UUID.fromString("457a8008-cb05-436d-97d1-7613a45258d7"),
                10,
                5
        );
    }

    private Item createItem() {
        return Item.builder()
                .id(UUID.fromString("271baaee-3228-411e-aedd-c1ca58e68cb6"))
                .product(createProduct())
                .inventory(createInventory())
                .currentQuantity(10)
                .minimumStockLevel(5)
                .build();
    }

    private Inventory createInventory() {
        return Inventory.builder()
                .id(UUID.fromString("457a8008-cb05-436d-97d1-7613a45258d7"))
                .name("Inventário B")
                .description("Descrição do Inventário B")
                .notificationEmail("aviso@user.com")
                .owner(createAuthUser())
                .build();
    }

    private Product createProduct() {
        return Product.builder()
                .id(UUID.fromString("cc60a7df-1ddd-47c6-bcc1-765f530bed6c"))
                .name("Produto C")
                .supplierCode("ABC123")
                .owner(createAuthUser())
                .build();
    }

    private AuthUser createAuthUser() {
        return AuthUser.builder()
                .id(UUID.fromString("4931241e-a79e-44f0-83fd-af904e933f29"))
                .email("user@email.com")
                .hashPassword("$2a$10$EIXom5ZM5Z")
                .roles(Set.of(Role.ROLE_USER))
                .build();
    }

    private ArgumentCaptor<Item> getItemArgumentCaptor() {
        return ArgumentCaptor.forClass(Item.class);
    }

    private List<UUID> getInventoryIdsList() {
        return List.of(
                UUID.fromString("703f20db-e8da-4145-9f02-a7c59f847631"),
                UUID.fromString("3d31fa59-9585-48cb-ac24-6b246697e844"),
                UUID.fromString("1e465076-1235-4c0e-9d27-ea1ca3c54cac"),
                UUID.fromString("b5915875-4750-4703-a9e1-a5c512cce2bc"),
                UUID.fromString("86c3fc30-e582-4571-afad-fd4ad9e089fc"),
                UUID.fromString("47bdf4cc-4c52-4716-a7c7-698b855ac220"),
                UUID.fromString("0d65c1c6-e67f-46f2-a35c-f74f81aef53c"),
                UUID.fromString("615ea3da-6d79-4bc8-bd18-02f910fb6ebb"),
                UUID.fromString("7bc0dac4-ec3f-4d15-b289-2d1ee3dddba5"),
                UUID.fromString("f55eacb8-7f16-4a4a-812e-2f7b6f6e6c70")
        );
    }

    private List<Item> createItemListWithDifferentInventories() {
        var inventoryIdsList = getInventoryIdsList();
        var itemList = new ArrayList<Item>();
        for (var i = 0; i < 10; i++) {
            var item = createItem().toBuilder()
                    .id(UUID.randomUUID())
                    .inventory(Inventory.builder()
                            .id(inventoryIdsList.get(i))
                            .name("Inventário " + (i + 1))
                            .description("Descrição do Inventário " + (i + 1))
                            .notificationEmail("aviso" + (i + 1) + "@user.com")
                            .owner(createAuthUser())
                            .build()
                    )
                    .product(Product.builder()
                            .id(UUID.randomUUID())
                            .name("Produto " + (i + 1))
                            .supplierCode("ABC" + (i + 1) + "123")
                            .owner(createAuthUser())
                            .build()
                    )
                    .currentQuantity(10 + i)
                    .minimumStockLevel(1 + i)
                    .build();
            itemList.add(item);
        }
        return itemList;
    }

    private List<Item> createItemListForTheSameInventory() {
        var inventory = createInventory();
        var itemList = new ArrayList<Item>();
        for (var i = 0; i < 10; i++) {
            var item = createItem().toBuilder()
                    .id(UUID.randomUUID())
                    .inventory(inventory)
                    .product(Product.builder()
                            .id(UUID.randomUUID())
                            .name("Produto " + (i + 1))
                            .supplierCode("ABC" + (i + 1) + "123")
                            .owner(createAuthUser())
                            .build()
                    )
                    .currentQuantity(10 + i)
                    .minimumStockLevel(1 + i)
                    .build();
            itemList.add(item);
        }
        return itemList;
    }

    private List<InventoryResponseDTO> getInventoryResponseDTOList() {
        var inventoryResponseDtoList = new ArrayList<InventoryResponseDTO>();
        var itemList = createItemListWithDifferentInventories();
        for (var item : itemList) {
            inventoryResponseDtoList.add(InventoryResponseDTO.from(item.getInventory()));
        }
        return inventoryResponseDtoList;
    }

    private List<Item> createLowStockItemList() {
        var inventoryIdsList = getInventoryIdsList();
        var lowStockItems = new ArrayList<Item>();
        for (var i = 0; i < 10; i++) {
            var item = createItem().toBuilder()
                    .id(UUID.randomUUID())
                    .inventory(Inventory.builder()
                            .id(inventoryIdsList.get(i))
                            .name("Inventário " + (i + 1))
                            .description("Descrição do Inventário " + (i + 1))
                            .notificationEmail("aviso" + (i + 1) + "@user.com")
                            .owner(createAuthUser())
                            .build()
                    )
                    .product(Product.builder()
                            .id(UUID.randomUUID())
                            .name("Produto " + (i + 1))
                            .supplierCode("ABC" + (i + 1) + "123")
                            .owner(createAuthUser())
                            .build()
                    )
                    .currentQuantity(2)
                    .minimumStockLevel(5)
                    .build();
            lowStockItems.add(item);
        }
        return lowStockItems;
    }
}
