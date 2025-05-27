package edu.infnet.InventorizeAPI.services;

import edu.infnet.InventorizeAPI.dto.request.InventoryRequestDTO;
import edu.infnet.InventorizeAPI.dto.response.InventoryResponseDTO;
import edu.infnet.InventorizeAPI.entities.Inventory;
import edu.infnet.InventorizeAPI.exceptions.custom.UnauthorizedRequestException;
import edu.infnet.InventorizeAPI.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

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

    public InventoryResponseDTO getById(@PathVariable UUID id) {
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

        var updatedInventory = inventory.updateFromDto(inventoryRequestDTO);

        var savedInventory = inventoryRepository.save(updatedInventory);

        return InventoryResponseDTO.from(savedInventory);
    }

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
    private Inventory validateOwnershipById(UUID id) {
        var inventory = inventoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Inventário não encontrado com o id: " + id));
        var currentUser = authenticationService.getAuthenticatedUser();

        if(!inventory.getOwner().getId().equals(currentUser.getId())) throw new UnauthorizedRequestException("Usuário não tem autorização para gerenciar este inventário.");

        return inventory;
    }
}
