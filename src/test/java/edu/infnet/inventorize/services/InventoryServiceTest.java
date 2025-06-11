package edu.infnet.inventorize.services;

import edu.infnet.inventorize.dto.request.inventory.InventoryDTO;
import edu.infnet.inventorize.dto.request.inventory.PatchInventoryDTO;
import edu.infnet.inventorize.dto.request.inventory.UpdateInventoryDTO;
import edu.infnet.inventorize.entities.AuthUser;
import edu.infnet.inventorize.entities.Inventory;
import edu.infnet.inventorize.enums.Role;
import edu.infnet.inventorize.exceptions.custom.InventoryNotFoundException;
import edu.infnet.inventorize.exceptions.custom.UnauthorizedRequestException;
import edu.infnet.inventorize.repository.InventoryRepository;
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
public class InventoryServiceTest {
    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private InventoryService inventoryService;


    // TESTES DE CRIAÇÃO -----------------------------------------------------------------------------------------------
    @Test
    public void shouldSaveInventoryWithCorrectData() {
        var inventoryDTO = getValidInventoryDTO();
        var user = getValidUser();
        var inventory = getValidInventory();
        var inventoryCaptor = getInventoryCaptor();

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        inventoryService.createInventory(inventoryDTO);

        verify(inventoryRepository).save(inventoryCaptor.capture());
        var savedInventory = inventoryCaptor.getValue();

        assertEquals(inventoryDTO.name(), savedInventory.getName(), "O nome do inventário salvo deve ser igual ao da requisição");
        assertEquals(inventoryDTO.description(), savedInventory.getDescription(), "A descrição do inventário salvo deve ser igual à da requisição");
        assertEquals(inventoryDTO.notificationEmail(), savedInventory.getNotificationEmail(), "O email de notificação do inventário salvo deve ser igual ao da requisição");
        assertEquals(user.getId(), savedInventory.getOwner().getId(), "O dono do inventário salvo deve ser o usuário autenticado");
    }

    @Test
    public void shouldRespondWithCorrectData() {
        var inventoryDTO = getValidInventoryDTO();
        var user = getValidUser();
        var inventory = getValidInventory();

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        var responseDTO = inventoryService.createInventory(inventoryDTO);

        assertEquals(inventory.getId(), responseDTO.id(), "O id retornado deve ser igual ao id do inventário criado");
        assertEquals(inventoryDTO.name(), responseDTO.name(), "O nome retornado deve ser igual ao da requisição");
        assertEquals(inventoryDTO.description(), responseDTO.description(), "A descrição retornada deve ser igual à da requisição");
        assertEquals(inventoryDTO.notificationEmail(), responseDTO.notificationEmail(), "O email de notificação retornado deve ser igual ao da requisição");
        assertEquals(user.getId(), responseDTO.ownerId(), "O dono do inventário retornado deve ser o usuário autenticado");
    }

    @Test
    public void shouldSaveInventoryWithEmptyDescription() {
        var emptyDescriptionDto = new InventoryDTO("Inventário B", null, "aviso@email.com");
        var user = getValidUser();
        var inventory = Inventory.builder()
                .id(UUID.fromString("4eb1d672-3fed-4012-bc1e-95e70b6ebdc4"))
                .name("Inventário B")
                .description("")
                .notificationEmail("aviso@email.com")
                .owner(getValidUser())
                .build();

        var inventoryCaptor = getInventoryCaptor();

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        inventoryService.createInventory(emptyDescriptionDto);

        verify(inventoryRepository).save(inventoryCaptor.capture());
        var savedInventory = inventoryCaptor.getValue();

        assertEquals("", savedInventory.getDescription(), "A descrição do inventário salvo deve ser uma string vazia quando não fornecida na requisição");
    }

    @Test
    public void shouldCallCorrectMethodsWhenCreatingInventory() {
        var inventoryDTO = getValidInventoryDTO();
        var user = getValidUser();
        var inventory = getValidInventory();

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        inventoryService.createInventory(inventoryDTO);

        verify(authenticationService, times(1)).getAuthenticatedUser();
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
        verifyNoMoreInteractions(authenticationService, inventoryRepository);
    }

    // TESTES DE RECUPERAÇÃO -------------------------------------------------------------------------------------------
    @Test
    public void shouldRespondWithCorrectDataWhenGettingById() {
        var inventory = getValidInventory();
        var user = getValidUser();

        when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.of(inventory));
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);

        var responseDTO = inventoryService.getById(inventory.getId());

        assertEquals(inventory.getId(), responseDTO.id(), "O id retornado deve ser igual ao id do inventário mockado");
        assertEquals(inventory.getName(), responseDTO.name(), "O nome retornado deve ser igual ao do inventário mockado");
        assertEquals(inventory.getDescription(), responseDTO.description(), "A descrição retornada deve ser igual à do inventário mockado");
        assertEquals(inventory.getNotificationEmail(), responseDTO.notificationEmail(), "O email de notificação retornado deve ser igual ao do inventário mockado");
        assertEquals(user.getId(), responseDTO.ownerId(), "O dono do inventário retornado deve ser o usuário autenticado");
    }

    @Test
    public void shouldCallCorrectMethodsGettingById() {
        var inventory = getValidInventory();
        var user = getValidUser();

        when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.of(inventory));
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);

        inventoryService.getById(inventory.getId());

        verify(inventoryRepository, times(1)).findById(inventory.getId());
        verify(authenticationService, times(1)).getAuthenticatedUser();
    }

    @Test
    public void shouldRespondGetAllWithCorrectData() {
        var user = getValidUser();
        var inventoryList = getValidInventories();

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(inventoryRepository.findByOwnerId(user.getId())).thenReturn(inventoryList);

        var responseDtoList = inventoryService.getAll();

        assertEquals(inventoryList.size(), responseDtoList.size());
        for (var i = 0; i < inventoryList.size(); i++) {
            assertEquals(inventoryList.get(i).getId(), responseDtoList.get(i).id(), "O id retornado deve ser igual ao id do inventário mockado");
            assertEquals(inventoryList.get(i).getName(), responseDtoList.get(i).name(), "O nome retornado deve ser igual ao do inventário mockado");
            assertEquals(inventoryList.get(i).getDescription(), responseDtoList.get(i).description(), "A descrição retornada deve ser igual à do inventário mockado");
            assertEquals(inventoryList.get(i).getNotificationEmail(), responseDtoList.get(i).notificationEmail(), "O email de notificação retornado deve ser igual ao do inventário mockado");
            assertEquals(user.getId(), responseDtoList.get(i).ownerId(), "O dono do inventário retornado deve ser o usuário autenticado");
        }
    }

    @Test
    public void shouldCallCorrectMethodsWhenGettingAll() {
        var user = getValidUser();
        var inventoryList = getValidInventories();

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(inventoryRepository.findByOwnerId(user.getId())).thenReturn(inventoryList);

        inventoryService.getAll();

        verify(authenticationService, times(1)).getAuthenticatedUser();
        verify(inventoryRepository, times(1)).findByOwnerId(user.getId());
    }

    // TESTES DE ATUALIZAÇÃO PARCIAL -----------------------------------------------------------------------------------
    @Test
    public void shouldPatchInventoryCorrectly() {
        var inventory = getValidInventory();
        var user = getValidUser();
        var patchDto = new PatchInventoryDTO("Inventário A-tualizado", "Uma nova descrição de inventário", null);
        var updatedInventory = inventory.toBuilder()
                .name(patchDto.name())
                .description(patchDto.description())
                .notificationEmail(patchDto.notificationEmail())
                .build();
        var captor = getInventoryCaptor();


        when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.of(inventory));
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(updatedInventory);

        var responseDto = inventoryService.patch(inventory.getId(), patchDto);

        verify(inventoryRepository).save(captor.capture());
        var savedInventory = captor.getValue();

        assertEquals(user.getId(), savedInventory.getOwner().getId(), "O dono do inventário salvo deve ser o usuário autenticado");

        assertEquals(patchDto.name(), savedInventory.getName(), "O nome salvo deve ser igual ao da requisição");
        assertEquals(patchDto.description(), savedInventory.getDescription(), "A descrição salva deve ser igual à da requisição");
        assertEquals(inventory.getNotificationEmail(), savedInventory.getNotificationEmail(), "O email de notificação deve permanecer inalterado");

        assertEquals(inventory.getId(), responseDto.id(), "O id retornado deve ser igual ao id do inventário mockado");
        assertEquals(inventory.getOwner().getId(), responseDto.ownerId(), "O dono do inventário retornado deve ser o usuário autenticado");
        assertEquals(patchDto.name(), responseDto.name(), "O nome retornado deve ser igual ao da requisição");
        assertEquals(patchDto.description(), responseDto.description(), "A descrição retornada deve ser igual à da requisição");
        assertEquals(patchDto.notificationEmail(), responseDto.notificationEmail(), "O email de notificação retornado deve ser igual ao da requisição");
    }

    @Test
    public void shouldPatchInventoryWithPartialData() {
        var inventory = getValidInventory();
        var user = getValidUser();
        var patchDto = new PatchInventoryDTO(null, null, "novoEmail@novo.com");
        var updatedInventory = inventory.toBuilder()
                .notificationEmail(patchDto.notificationEmail())
                .build();
        var captor = getInventoryCaptor();

        when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.of(inventory));
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(updatedInventory);

        var responseDto = inventoryService.patch(inventory.getId(), patchDto);
        verify(inventoryRepository).save(captor.capture());
        var savedInventory = captor.getValue();

        assertEquals(user.getId(), savedInventory.getOwner().getId(), "O dono do inventário salvo deve ser o usuário autenticado");

        assertEquals(inventory.getName(), savedInventory.getName(), "O nome deve permanecer inalterado");
        assertEquals(inventory.getDescription(), savedInventory.getDescription(), "A descrição deve permanecer inalterada");
        assertEquals(updatedInventory.getNotificationEmail(), savedInventory.getNotificationEmail(), "O email de notificação deve ser atualizado");
        assertEquals(inventory.getOwner().getId(), savedInventory.getOwner().getId(), "O dono do inventário salvo deve permanecer o mesmo");

        assertEquals(inventory.getId(), responseDto.id(), "O id retornado deve ser igual ao id do inventário mockado");
        assertEquals(inventory.getOwner().getId(), responseDto.ownerId(), "O dono do inventário retornado deve ser o usuário autenticado");
        assertEquals(inventory.getName(), responseDto.name(), "O nome retornado não deve ser alterado");
        assertEquals(inventory.getDescription(), responseDto.description(), "A descrição retornada não deve ser alterada");
        assertEquals(updatedInventory.getNotificationEmail(), responseDto.notificationEmail(), "O email de notificação retornado deve ser igual ao da requisição");
    }

    @Test
    public void shouldNotPatchInventory() {
        var inventory = getValidInventory();
        var user = getValidUser();
        var patchDto = new PatchInventoryDTO(null, null, null);
        var captor = getInventoryCaptor();

        when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.of(inventory));
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);

        var responseDto = inventoryService.patch(inventory.getId(), patchDto);
        verify(inventoryRepository).save(captor.capture());
        var savedInventory = captor.getValue();

        assertEquals(user.getId(), savedInventory.getOwner().getId(), "O dono do inventário salvo deve ser o usuário autenticado");

        assertEquals(inventory.getName(), savedInventory.getName(), "O nome deve permanecer inalterado");
        assertEquals(inventory.getDescription(), savedInventory.getDescription(), "A descrição deve permanecer inalterada");
        assertEquals(inventory.getNotificationEmail(), savedInventory.getNotificationEmail(), "O email de notificação deve permanecer inalterado");
        assertEquals(inventory.getOwner().getId(), savedInventory.getOwner().getId(), "O dono do inventário salvo deve permanecer o mesmo");

        assertEquals(inventory.getId(), responseDto.id(), "O id retornado deve ser igual ao id do inventário mockado");
        assertEquals(inventory.getOwner().getId(), responseDto.ownerId(), "O dono do inventário retornado deve ser o usuário autenticado");
        assertEquals(inventory.getName(), responseDto.name(), "O nome retornado não deve ser alterado");
        assertEquals(inventory.getDescription(), responseDto.description(), "A descrição retornada não deve ser alterada");
        assertEquals(inventory.getNotificationEmail(), responseDto.notificationEmail(), "O email de notificação não deve ser alterado");
    }

    @Test
    public void shouldCallCorrectMethodsWhenPatchingInventory() {
        var inventory = getValidInventory();
        var user = getValidUser();
        var patchDto = new PatchInventoryDTO("Inventário A-tualizado", "Uma nova descrição de inventário", "novoEmail@novo.com");
        var updatedInventory = inventory.toBuilder()
                .name(patchDto.name())
                .description(patchDto.description())
                .notificationEmail(patchDto.notificationEmail())
                .build();

        when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.of(inventory));
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(updatedInventory);

        inventoryService.patch(inventory.getId(), patchDto);

        verify(authenticationService, times(1)).getAuthenticatedUser();
        verify(inventoryRepository, times(1)).findById(inventory.getId());
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    // TESTES DE ATUALIZAÇÃO TOTAL -------------------------------------------------------------------------------------
    @Test
    public void shouldPutInventoryCorrectly() {
        var inventory = getValidInventory();
        var user = getValidUser();
        var updateInventoryDTO = new UpdateInventoryDTO("Inventário A-tualizado", "Uma nova descrição de inventário", "novo-email@gmail.com");
        var updatedInventory = inventory.toBuilder()
                .name(updateInventoryDTO.name())
                .description(updateInventoryDTO.description())
                .notificationEmail(updateInventoryDTO.notificationEmail())
                .build();
        var captor = getInventoryCaptor();

        when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.of(inventory));
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(updatedInventory);

        var responseDto = inventoryService.update(inventory.getId(), updateInventoryDTO);
        verify(inventoryRepository).save(captor.capture());
        var savedInventory = captor.getValue();

        assertEquals(user.getId(), savedInventory.getOwner().getId(), "O dono do inventário salvo deve ser o usuário autenticado");

        assertEquals(updateInventoryDTO.name(), savedInventory.getName(), "O nome do inventário salvo deve ser igual ao da requisição");
        assertEquals(updateInventoryDTO.description(), savedInventory.getDescription(), "A descrição retornada deve ser igual à da requisição");
        assertEquals(updateInventoryDTO.notificationEmail(), savedInventory.getNotificationEmail(), "O email de notificação retornado deve ser igual ao da requisição");
        assertEquals(updatedInventory.getOwner().getId(), savedInventory.getOwner().getId(), "O dono do inventário salvo deve permanecer o mesmo");

        assertEquals(inventory.getId(), responseDto.id(), "O id do inventário retornado deve permanecer inalterado");
        assertEquals(inventory.getOwner().getId(), responseDto.ownerId(), "O dono do inventário deve permanecer inalterado");
        assertEquals(updateInventoryDTO.name(), responseDto.name(), "O nome do inventário retornado deve ser igual ao da requisição");
        assertEquals(updateInventoryDTO.description(), responseDto.description(), "A descrição do inventário retornado deve ser igual à da requisição");
        assertEquals(updateInventoryDTO.notificationEmail(), responseDto.notificationEmail(), "O email de notificação deve ser igual ao da requisição");
    }

    @Test
    public void shouldCallCorrectMethodsWhenPuttingInventory() {
        var inventory = getValidInventory();
        var user = getValidUser();
        var updateInventoryDTO = new UpdateInventoryDTO("Inventário A-tualizado", "Uma nova descrição de inventário", "novo-email@gmail.com");
        var updatedInventory = inventory.toBuilder()
                .name(updateInventoryDTO.name())
                .description(updateInventoryDTO.description())
                .notificationEmail(updateInventoryDTO.notificationEmail())
                .build();

        when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.of(inventory));
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(updatedInventory);

        inventoryService.update(inventory.getId(), updateInventoryDTO);

        verify(inventoryRepository, times(1)).findById(inventory.getId());
        verify(authenticationService, times(1)).getAuthenticatedUser();
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
        verifyNoMoreInteractions(inventoryRepository, authenticationService);
    }


    // TESTES DE DELEÇÃO -----------------------------------------------------------------------------------------------
    @Test
    public void shouldDeleteInventoryCorrectly() {
        var inventory = getValidInventory();
        var user = getValidUser();

        when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.of(inventory));
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);

        inventoryService.delete(inventory.getId());

        verify(inventoryRepository, times(1)).delete(inventory);
    }

    // TESTES DE VALIDAÇÃO DE PROPRIEDADE ------------------------------------------------------------------------------
    @Test
    public void shouldValidateOwnershipById() {
        var inventory = getValidInventory();
        var user = getValidUser();

        when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.of(inventory));
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);

        var foundInventory = inventoryService.validateOwnershipById(inventory.getId());

        assertEquals(user.getId(), foundInventory.getOwner().getId(), "O dono do inventário encontrado deve ser o usuário autenticado");
    }

    @Test
    public void shouldThrowExceptionWhenInventoryNotFound() {
        var inventory = getValidInventory();

        when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.empty());

        var inventoryNotFoundException = assertThrows(InventoryNotFoundException.class,
                () -> inventoryService.validateOwnershipById(inventory.getId()),
                "Deve lançar uma exceção quando o inventário não for encontrado");

        assertEquals("Inventário com o [ ID: %s ] não encontrado".formatted(inventory.getId()), inventoryNotFoundException.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenUnauthorizedAccess() {
        var inventory = getValidInventory();
        var unauthorizedUser = AuthUser.builder()
                .id(UUID.fromString("04b1ea21-5872-4679-bda3-71d39400ca77"))
                .build();

        when(inventoryRepository.findById(inventory.getId())).thenReturn(Optional.of(inventory));
        when(authenticationService.getAuthenticatedUser()).thenReturn(unauthorizedUser);

        assertThrows(UnauthorizedRequestException.class,
                () -> inventoryService.validateOwnershipById(inventory.getId()),
                "Deve lançar uma exceção quando o usuário não for o dono do inventário");
    }


    // MÉTODOS UTILITÁRIOS ----------------------------------------------------------------------------------------------
    private InventoryDTO getValidInventoryDTO() {
        return new InventoryDTO("Inventário B", "Inventário de teste", "aviso@email.com");
    }

    private AuthUser getValidUser() {
        return AuthUser.builder()
                .id(UUID.fromString("6e399d0f-66ad-4092-97e7-1d755388959f"))
                .email("user@email.com")
                .hashPassword("$2a$10$eImiTMZG4ELQ2Z8z5y3jOe")
                .roles(Set.of(Role.ROLE_USER))
                .build();
    }

    private Inventory getValidInventory() {
        return Inventory.builder()
                .id(UUID.fromString("4eb1d672-3fed-4012-bc1e-95e70b6ebdc4"))
                .name("Inventário B")
                .description("Inventário de teste")
                .notificationEmail("aviso@email.com")
                .owner(getValidUser())
                .build();
    }

    private List<Inventory> getValidInventories() {
        var validUser = getValidUser();
        var newInventoryList = new ArrayList<Inventory>();
        for (var i = 1; i <= 10; i++) {
            newInventoryList.add(
                    Inventory.builder()
                            .id(UUID.randomUUID())
                            .name("Inventário " + i)
                            .description("Descrição do inventário " + i)
                            .notificationEmail("avisoInventario_" + i + "@email.com")
                            .owner(validUser)
                            .build()
            );
        }
        return newInventoryList;
    }

    private ArgumentCaptor<Inventory> getInventoryCaptor() {
        return ArgumentCaptor.forClass(Inventory.class);
    }
}
