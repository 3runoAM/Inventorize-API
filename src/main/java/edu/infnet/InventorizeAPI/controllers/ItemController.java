package edu.infnet.InventorizeAPI.controllers;

import edu.infnet.InventorizeAPI.dto.request.item.ItemDTO;
import edu.infnet.InventorizeAPI.dto.request.item.PatchItemDTO;
import edu.infnet.InventorizeAPI.dto.request.item.UpdateItemDTO;
import edu.infnet.InventorizeAPI.dto.response.ItemResponseDTO;
import edu.infnet.InventorizeAPI.services.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Controller de Itens", description = "Endpoints para gerenciamento de itens")
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    /**
     * Cria um novo item de inventário.
     *
     * @param itemRequest dados do item a ser criado
     * @return informações do item criado
     */
    @Operation(
            summary = "Cria um novo Item",
            description = "Cria um item e retorna suas informações: O id do produto e do inventário, o limite de estoque" +
                    "e a quantidade atual."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Item criado com sucesso",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "productId": "a0d7e008-e953-4b97-95a9-f73ece8d2f0d",
                                                      "inventoryId": "8dbce360-0e12-4eb3-b9b6-eaaefdb34192",
                                                      "currentQuantity": "10",
                                                      "minimumStockLevel": "5"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou ausentes",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "message": "Dados inválidos ou ausentes",
                                                      "errorDetails": [
                                                        "O limite crítico de estoque do item é obrigatório neste contexto",
                                                        "O limite crítico de estoque do item deve ser zero ou positivo",
                                                        "A quantidade atual do item é obrigatória neste contexto",
                                                        "A quantidade atual do item deve ser zero ou positiva"
                                                      ],
                                                      "timestamp": "2024-06-10T15:30:00"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acesso negado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "status": 401,
                                                      "message": "Credenciais inválidas",
                                                      "errorDetails": [
                                                          "Usuário inexistente ou senha inválida"
                                                      ],
                                                      "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                    }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso proibido",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {}
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {}
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ItemResponseDTO> createItem(@Valid @RequestBody ItemDTO itemRequest) {
        ItemResponseDTO itemInfo = itemService.create(itemRequest);

        return ResponseEntity.status(201).body(itemInfo);
    }

    /**
     * Busca um item de inventário pelo seu ID.
     *
     * @param id identificador do item
     * @return informações do item encontrado
     */
    @Operation(
            summary = "Busca um Item pelo ID",
            description = "Retorna as informações do item correspondente ao ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Item recuperado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "id": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                      "productId": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                      "inventoryId": "66e1e3c1-2548-4a68-9a02-0ac0bf62ae52",
                                                      "currentQuantity": "50",
                                                      "minimumStockLevel": "10"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acesso negado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "status": 401,
                                                      "message": "Credenciais inválidas",
                                                      "errorDetails": [
                                                          "Usuário inexistente ou senha inválida"
                                                      ],
                                                      "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                    }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Acesso proibido",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {}
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Item não encontrado",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                       "status": 404,
                                                       "message": "Item não encontrado",
                                                       "errorDetails": "Item com o [ ID: b947fb21-fa2d-4e41-93c5-b3f4cf425afb ] não encontrado",
                                                       "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                     }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro ao atualizar o inventário",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {}"""
                                    )
                            }
                    )
            ),
    })
    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> getItem(@PathVariable UUID id) {
        ItemResponseDTO itemInfo = itemService.getById(id);

        return ResponseEntity.ok(itemInfo);
    }

    /**
     * Lista todos os itens de inventário.
     *
     * @return lista de itens de inventário
     */
    @Operation(
            summary = "Recupera todos os itens",
            description = "Recupera todos os items do usuário autenticado.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de itens recuperada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    [
                                                      {
                                                        "id": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                        "productId": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                        "inventoryId": "66e1e3c1-2548-4a68-9a02-0ac0bf62ae52",
                                                        "currentQuantity": "25",
                                                        "minimumStockLevel": "2"
                                                      },
                                                      {
                                                        "id": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                        "productId": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                        "inventoryId": "66e1e3c1-2548-4a68-9a02-0ac0bf62ae52",
                                                        "currentQuantity": "10",
                                                        "minimumStockLevel": "1"
                                                      }
                                                    ]
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Você não tem permissão para acessar os inventários",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            value = "{}"
                                    )
                            }
                    )
            ),
    })
    @GetMapping
    public ResponseEntity<List<ItemResponseDTO>> getAllItems() {
        List<ItemResponseDTO> items = itemService.getAll();

        return ResponseEntity.ok(items);
    }

    /**
     * Lista todos os itens de um inventário específico.
     *
     * @param id identificador do inventário
     * @return lista de itens pertencentes ao inventário
     */
    @Operation(
            summary = "Recupera todos os itens de um inventário específico",
            description = "Recupera todos os items de um inventário específico")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de itens recuperada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    [
                                                      {
                                                        "id": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                        "productId": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                        "inventoryId": "66e1e3c1-2548-4a68-9a02-0ac0bf62ae52",
                                                        "currentQuantity": "25",
                                                        "minimumStockLevel": "2"
                                                      },
                                                      {
                                                        "id": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                        "productId": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                        "inventoryId": "66e1e3c1-2548-4a68-9a02-0ac0bf62ae52",
                                                        "currentQuantity": "10",
                                                        "minimumStockLevel": "1"
                                                      }
                                                    ]
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Você não tem permissão para acessar esse item",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            value = "{}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Inventário não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                       "status": 404,
                                                       "message": "Inventário não encontrado",
                                                       "errorDetails": "Inventário com o [ ID: b947fb21-fa2d-4e41-93c5-b3f4cf425afb ] não encontrado",
                                                       "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                     }"""
                                    )
                            }
                    )
            ),
    })
    @GetMapping("/inventory/{id}")
    public ResponseEntity<List<ItemResponseDTO>> getAllByInventory(@PathVariable UUID id) {
        List<ItemResponseDTO> items = itemService.getAllItemsByInventoryId(id);

        return ResponseEntity.ok(items);
    }

    /**
     * Atualiza completamente um item de inventário.
     *
     * @param id          identificador do item
     * @param itemRequest dados atualizados do item
     * @return informações do item atualizado
     */
    @Operation(
            summary = "Atualiza todos os campos de um item pelo ID",
            description = "Atualiza todos os campos de um item existente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Item atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                      {
                                                        "id": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                        "productId": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                        "inventoryId": "66e1e3c1-2548-4a68-9a02-0ac0bf62ae52",
                                                        "currentQuantity": "25",
                                                        "minimumStockLevel": "2"
                                                      }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou ausentes",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "status": 400,
                                                        "message": "Erro de validação",
                                                        "errorDetails": [
                                                            "A quantidade atual do item é obrigatória neste contexto",
                                                            "A quantidade atual do item deve ser zero ou positiva",
                                                            "O limite crítico de estoque do item é obrigatório neste contexto",
                                                            "O limite crítico de estoque do item deve ser zero ou positivo"
                                                        ],
                                                        "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                    }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Você não tem permissão para atualizar este item",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            value = "{}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Item não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                       "status": 404,
                                                       "message": "Item não encontrado",
                                                       "errorDetails": "Item com o [ ID: b947fb21-fa2d-4e41-93c5-b3f4cf425afb ] não encontrado",
                                                       "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                     }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro ao atualizar o item",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {}"""
                                    )
                            }
                    )
            ),
    })
    @PutMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> updateInventoryItem(@PathVariable UUID id, @Valid @RequestBody UpdateItemDTO itemRequest) {
        ItemResponseDTO updatedItem = itemService.update(id, itemRequest);

        return ResponseEntity.ok(updatedItem);
    }

    /**
     * Atualiza parcialmente um item de inventário.
     *
     * @param id          identificador do item
     * @param itemRequest dados parciais para atualização
     * @return informações do item atualizado
     */
    @Operation(
            summary = "Atualiza parcialmente um item pelo ID",
            description = "Atualiza os campos de um item existente, como quantidade atual e/ou limite de estoque"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Item atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                    "id": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                    "productId": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                    "inventoryId": "66e1e3c1-2548-4a68-9a02-0ac0bf62ae52",
                                                    "currentQuantity": "25",
                                                    "minimumStockLevel": "10"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou ausentes",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "message": "Erro de validação",
                                                      "errorDetails": [
                                                        "O limite crítico de estoque do item deve ser zero ou positivo",
                                                        "A quantidade atual do item deve ser zero ou positiva"
                                                      ],
                                                      "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                    }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Você não tem permissão para atualizar este inventário",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            value = "{}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Item não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                       "status": 404,
                                                       "message": "Item não encontrado",
                                                       "errorDetails": "Item com o [ ID: b947fb21-fa2d-4e41-93c5-b3f4cf425afb ] não encontrado",
                                                       "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                     }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {}
                                                    """
                                    )
                            }
                    )
            ),
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> patchInventoryItem(@PathVariable UUID id, @Valid @RequestBody PatchItemDTO itemRequest) {
        ItemResponseDTO patchedItem = itemService.patch(id, itemRequest);

        return ResponseEntity.ok(patchedItem);
    }

    /**
     * Atualiza a quantidade atual de um item de inventário.
     *
     * @param id         identificador do item
     * @param adjustment quantidade a ser ajustada (pode ser positiva ou negativa)
     * @return informações do item atualizado
     */
    @Operation(
            summary = "Ajusta a quantidade atual de um item pelo ID",
            description = "Ajusta a quantidade atual do item em estoque."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "401",
                    description = "Acesso negado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "status": 401,
                                                      "message": "Credenciais inválidas",
                                                      "errorDetails": [
                                                          "Usuário inexistente ou senha inválida"
                                                      ],
                                                      "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                    }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Você não tem permissão para atualizar este inventário",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            value = "{}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Item não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                       "status": 404,
                                                       "message": "Item não encontrado",
                                                       "errorDetails": "Item com o [ ID: b947fb21-fa2d-4e41-93c5-b3f4cf425afb ] não encontrado",
                                                       "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                     }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Ajuste inválido",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "status": 422,
                                                      "message": "Ajuste inválido",
                                                      "errorDetails": [
                                                        "A quantidade ajustada não pode resultar em valor negativo"
                                                      ],
                                                      "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                    }"""
                                    )
                            }
                    )
            )
    })
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
    @Operation(
            summary = "Deleta um item pelo ID",
            description = "Remove um item específico pelo seu ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Item deletado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "message": "Item deletado com sucesso"
                                                    }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Você não tem permissão para deletar este item",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            value = "{}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Item não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                       "status": 404,
                                                       "message": "Item não encontrado",
                                                       "errorDetails": "Item com o [ ID: b947fb21-fa2d-4e41-93c5-b3f4cf425afb ] não encontrado",
                                                       "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                     }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro ao deletar o inventário",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {}
                                                    """
                                    )
                            }
                    )
            ),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable UUID id) {
        itemService.deleteById(id);

        return ResponseEntity.ok("Item deletado com sucesso");
    }

    /**
     * Recupera todos os itens que estão abaixo do limite mínimo de estoque.
     *
     * @return Lista de produtos abaixo do limite.
     */
    @Operation(
            summary = "Recupera todos os itens abaixo do limite de estoque",
            description = "Recupera todos os itens abaixo do limite de estoque")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de itens recuperada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    [
                                                      {
                                                        "id": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                        "productId": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                        "inventoryId": "66e1e3c1-2548-4a68-9a02-0ac0bf62ae52",
                                                        "currentQuantity": "0",
                                                        "minimumStockLevel": "2"
                                                      },
                                                      {
                                                        "id": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                        "productId": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                        "inventoryId": "66e1e3c1-2548-4a68-9a02-0ac0bf62ae52",
                                                        "currentQuantity": "0",
                                                        "minimumStockLevel": "1"
                                                      }
                                                    ]
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Você não tem permissão para acessar os inventários",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            value = "{}"
                                    )
                            }
                    )
            ),
    })
    @GetMapping("/low-stock")
    public ResponseEntity<List<ItemResponseDTO>> getLowStockItems() {
        List<ItemResponseDTO> lowStockItems = itemService.getLowStockItems();

        return ResponseEntity.ok(lowStockItems);
    }
}
