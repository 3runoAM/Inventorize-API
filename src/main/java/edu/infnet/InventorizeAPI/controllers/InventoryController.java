package edu.infnet.InventorizeAPI.controllers;

import edu.infnet.InventorizeAPI.dto.request.inventory.InventoryDTO;
import edu.infnet.InventorizeAPI.dto.request.inventory.PatchInventoryDTO;
import edu.infnet.InventorizeAPI.dto.request.inventory.UpdateInventoryDTO;
import edu.infnet.InventorizeAPI.dto.response.InventoryResponseDTO;
import edu.infnet.InventorizeAPI.services.InventoryService;
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
@RequestMapping("/inventories")
public class InventoryController {
    private final InventoryService inventoryService;

    /**
     * Cria um novo inventário.
     *
     * @param inventoryDTO Dados do inventário a ser criado.
     * @return Informações do inventário criado.
     */
    @PostMapping
    public ResponseEntity<InventoryResponseDTO> createInventory(@Valid @RequestBody InventoryDTO inventoryDTO) {
        InventoryResponseDTO newInventory = inventoryService.createInventory(inventoryDTO);

        return ResponseEntity.ok(newInventory);
    }

    /**
     * Busca um inventário pelo seu ID.
     *
     * @param id Identificador do inventário.
     * @return Informações do inventário encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponseDTO> getInventory(@PathVariable UUID id) {
        InventoryResponseDTO inventory = inventoryService.getById(id);

        return ResponseEntity.ok(inventory);
    }

    /**
     * Lista todos os inventários do usuário autenticado.
     *
     * @return Lista de inventários.
     */
    @GetMapping
    public ResponseEntity<List<InventoryResponseDTO>> getAllInventories() {
        List<InventoryResponseDTO> inventories = inventoryService.getAll();

        return ResponseEntity.ok(inventories);
    }

    /**
     * Atualiza um inventário existente.
     *
     * @param id Identificador do inventário a ser atualizado.
     * @param inventoryDTO Dados do inventário a ser atualizado.
     * @return Informações do inventário atualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponseDTO> putInventory(@PathVariable UUID id, @Valid @RequestBody UpdateInventoryDTO inventoryDTO) {
        InventoryResponseDTO updatedInventory = inventoryService.update(id, inventoryDTO);

        return ResponseEntity.ok(updatedInventory);
    }

    /**
     * Atualiza parcialmente um inventário existente.
     *
     * @param id Identificador do inventário a ser atualizado.
     * @param inventoryRequestDTO Dados do inventário a ser atualizado.
     * @return Informações do inventário atualizado.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<InventoryResponseDTO> patchInventory(@PathVariable UUID id, @Valid @RequestBody PatchInventoryDTO inventoryRequestDTO) {
        InventoryResponseDTO updatedInventory = inventoryService.patch(id, inventoryRequestDTO);

        return ResponseEntity.ok(updatedInventory);
    }

    /**
     * Deleta um inventário pelo seu ID.
     *
     * @param id Identificador do inventário a ser deletado.
     * @return Mensagem de sucesso.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        inventoryService.delete(id);

        return ResponseEntity.ok("| Inventário deletado com sucesso.");
    }
}