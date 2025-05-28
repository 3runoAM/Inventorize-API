package edu.infnet.InventorizeAPI.services;

import edu.infnet.InventorizeAPI.dto.request.InventoryRequestDTO;
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

    public InventoryResponseDTO getById(UUID id) {
        Inventory inventory = validateOwnershipById(id);

        return InventoryResponseDTO.from(inventory);
    }

    public List<InventoryResponseDTO> getAll() {
        var currentUser = authenticationService.getAuthenticatedUser();
        return inventoryRepository.findByOwnerId(currentUser.getId())
                .stream()
                .map(InventoryResponseDTO::from)
                .toList();
    }

    @Transactional
    public InventoryResponseDTO patch(UUID inventoryId, InventoryRequestDTO inventoryRequestDTO) {
        Inventory inventory = validateOwnershipById(inventoryId);

        var inventoryBuilder = inventory.toBuilder();

        if (inventoryRequestDTO.name() != null) inventoryBuilder.name(inventoryRequestDTO.name());
        if (inventoryRequestDTO.description() != null) inventoryBuilder.description(inventoryRequestDTO.description());
        if (inventoryRequestDTO.notificationEmail() != null) inventoryBuilder.notificationEmail(inventoryRequestDTO.notificationEmail());

        var savedInventory = inventoryRepository.save(inventoryBuilder.build());

        return InventoryResponseDTO.from(savedInventory);
    }

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

    public void delete(UUID id) {
        var inventory = validateOwnershipById(id);
        inventoryRepository.delete(inventory);
    }

    // Métodos utilitários
    protected Inventory validateOwnershipById(UUID inventoryId) {
        var inventory = inventoryRepository.findById(inventoryId).orElseThrow(() -> new InventoryNotFoundException("Inventário com o [ ID: %s ] não encontrado com o id: ".formatted(inventoryId)));
        AuthUser currentUser = authenticationService.getAuthenticatedUser();

        if(!inventory.getOwner().equals(currentUser)) throw new UnauthorizedRequestException("Usuário não tem autorização para gerenciar este inventário.");

        return inventory;
    }

    protected List<Inventory> getAllByOwnerId(UUID ownerId) {
        return inventoryRepository.findByOwnerId(ownerId);
    }
}
