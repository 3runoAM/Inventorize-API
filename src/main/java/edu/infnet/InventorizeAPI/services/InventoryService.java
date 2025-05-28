package edu.infnet.InventorizeAPI.services;

import edu.infnet.InventorizeAPI.dto.request.InventoryRequestDTO;
import edu.infnet.InventorizeAPI.dto.request.PatchInventoryDTO;
import edu.infnet.InventorizeAPI.dto.response.InventoryResponseDTO;
import edu.infnet.InventorizeAPI.entities.AuthUser;
import edu.infnet.InventorizeAPI.entities.Inventory;
import edu.infnet.InventorizeAPI.exceptions.custom.InventoryNotFoundException;
import edu.infnet.InventorizeAPI.exceptions.custom.UnauthorizedRequestException;
import edu.infnet.InventorizeAPI.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final AuthenticationService authenticationService;
    private final InventoryRepository inventoryRepository;


    /**
     * Cria um novo inventário.
     *
     * @param inventoryRequestDTO dados do inventário a ser criado
     * @return informações do inventário criado
     */
    public InventoryResponseDTO createInventory(InventoryRequestDTO inventoryRequestDTO) {
        var newInventory = Inventory.builder()
                .name(inventoryRequestDTO.name())
                .description(inventoryRequestDTO.description())
                .notificationEmail(inventoryRequestDTO.notificationEmail())
                .owner(authenticationService.getAuthenticatedUser())
                .build();

        var savedInventory = inventoryRepository.save(newInventory);

        return InventoryResponseDTO.from(savedInventory);
    }

    /**
     * Busca um inventário pelo seu ID.
     *
     * @param id identificador do inventário
     * @return informações do inventário encontrado
     */
    public InventoryResponseDTO getById(UUID id) {
        Inventory inventory = validateOwnershipById(id);

        return InventoryResponseDTO.from(inventory);
    }

    /**
     * Busca todos os inventários do usuário autenticado.
     *
     * @return lista de inventários do usuário
     */
    public List<InventoryResponseDTO> getAll() {
        var currentUser = authenticationService.getAuthenticatedUser();
        return inventoryRepository.findByOwnerId(currentUser.getId())
                .stream()
                .map(InventoryResponseDTO::from)
                .toList();
    }

    /**
     * Atualiza parcialmente um inventário existente.
     *
     * @param inventoryId identificador do inventário a ser atualizado
     * @param inventoryRequestDTO dados atualizados do inventário
     * @return informações do inventário atualizado
     */
    @Transactional
    public InventoryResponseDTO patch(UUID inventoryId, PatchInventoryDTO inventoryRequestDTO) {
        Inventory inventory = validateOwnershipById(inventoryId);

        var inventoryBuilder = inventory.toBuilder();

        if (inventoryRequestDTO.name() != null) inventoryBuilder.name(inventoryRequestDTO.name());
        if (inventoryRequestDTO.description() != null) inventoryBuilder.description(inventoryRequestDTO.description());
        if (inventoryRequestDTO.notificationEmail() != null) inventoryBuilder.notificationEmail(inventoryRequestDTO.notificationEmail());

        var savedInventory = inventoryRepository.save(inventoryBuilder.build());

        return InventoryResponseDTO.from(savedInventory);
    }

    /**
     * Atualiza um inventário existente.
     *
     * @param id identificador do inventário a ser atualizado
     * @param inventoryRequestDTO dados atualizados do inventário
     * @return informações do inventário atualizado
     */
    @Transactional
    public InventoryResponseDTO update(UUID id, InventoryRequestDTO inventoryRequestDTO) {
        Inventory inventory = validateOwnershipById(id);

        var newInventory = Inventory.builder()
                .id(id)
                .owner(inventory.getOwner())
                .name(inventoryRequestDTO.name())
                .notificationEmail(inventoryRequestDTO.notificationEmail())
                .description(inventoryRequestDTO.description())
                .build();

        var savedInventory = inventoryRepository.save(newInventory);

        return InventoryResponseDTO.from(savedInventory);
    }

    /**
     * Deleta um inventário pelo seu ID.
     *
     * @param id identificador do inventário a ser deletado
     */
    public void delete(UUID id) {
        var inventory = validateOwnershipById(id);
        inventoryRepository.delete(inventory);
    }

    /**
     * Valida se o usuário autenticado é o proprietário do inventário.
     *
     * @param inventoryId ID do inventário a ser validado
     * @return o inventário se o usuário for o proprietário
     * @throws InventoryNotFoundException se o inventário não for encontrado
     * @throws UnauthorizedRequestException se o usuário não for o proprietário
     */
    protected Inventory validateOwnershipById(UUID inventoryId) {
        var inventory = inventoryRepository.findById(inventoryId).orElseThrow(() -> new InventoryNotFoundException("Inventário com o [ ID: %s ] não encontrado com o id: ".formatted(inventoryId)));
        AuthUser currentUser = authenticationService.getAuthenticatedUser();

        if(!inventory.getOwner().equals(currentUser)) throw new UnauthorizedRequestException("Usuário não tem autorização para gerenciar este inventário.");

        return inventory;
    }
}
