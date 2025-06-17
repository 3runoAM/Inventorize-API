package edu.infnet.inventorize.controllers;

import edu.infnet.inventorize.dto.request.inventory.InventoryDTO;
import edu.infnet.inventorize.dto.request.inventory.PatchInventoryDTO;
import edu.infnet.inventorize.dto.request.inventory.UpdateInventoryDTO;
import edu.infnet.inventorize.dto.response.InventoryResponseDTO;
import edu.infnet.inventorize.services.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Validated
@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Controller de Estoque", description = "Endpoints para gerenciamento de estoques")
@RequestMapping("/inventories")
public class InventoryController {
    private final InventoryService inventoryService;

    /**
     * Cria um novo inventário.
     *
     * @param inventoryDTO Dados do inventário a ser criado.
     * @return Informações do inventário criado.
     */
    @Operation(
            summary = "Cria um novo inventário",
            description = "Cria um novo inventário com nome, descrição e email para notificação de estoque baixo " +
                    "e retorna as informações do inventário criado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Inventário criado com sucesso",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "id": "d82e6d8c-c2d6-4903-bb93-79f7a5c50170",
                                                      "name": "Tintas acrílicas",
                                                      "description": "Inventário de tintas acrílicas para pintura artística",
                                                      "notificationEmail": "aviso@email.com",
                                                      "ownerId": "bcbbbe5d-434e-4bc7-81ec-962f6c1d6b58",
                                                      "_links" : {
                                                        "self": {
                                                          "href": "http://localhost:8080/inventorize/v1/inventories/d82e6d8c-c2d6-4903-bb93-79f7a5c50170"
                                                        },
                                                        "delete": {
                                                          "href": "http://localhost:8080/inventorize/v1/inventories/d82e6d8c-c2d6-4903-bb93-79f7a5c50170"
                                                        }
                                                      }
                                                    }"""
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
                                                        "O nome do inventário é obrigatório",
                                                        "O e-mail para notificações é obrigatório"
                                                      ],
                                                      "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                    }"""
                                    )
                            }
                    )
            ),
    })
    @PostMapping
    public ResponseEntity<EntityModel<InventoryResponseDTO>> createInventory(@Valid @RequestBody InventoryDTO inventoryDTO) {
        InventoryResponseDTO newInventory = inventoryService.createInventory(inventoryDTO);

        EntityModel<InventoryResponseDTO> resource = EntityModel.of(newInventory,
                linkTo(methodOn(InventoryController.class).getInventory(newInventory.id())).withRel("self"),
                linkTo(methodOn(InventoryController.class).delete(newInventory.id())).withRel("delete"));

        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    /**
     * Busca um inventário pelo seu ID.
     *
     * @param id Identificador do inventário.
     * @return Informações do inventário encontrado.
     */
    @Operation(
            summary = "Busca um inventário pelo ID",
            description = "Retorna as informações de um inventário específico pelo seu ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Inventário encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "id": "a1d2fb3a-dc03-4e2e-a09d-2a94f5670ed9",
                                                      "name": "Tintas acrílicas",
                                                      "description": "Inventário de tintas acrílicas para pintura artística",
                                                      "notificationEmail": "examploUser@email.com",
                                                      "ownerId": "bf42f203-aacb-43dd-a033-a05fd59267db",
                                                      "_links": {
                                                        "delete": {
                                                          "href": "http://localhost:8080/inventorize/v1/inventories/1dd1e937-a9cb-451c-98d5-3430737c80d5"
                                                        },
                                                      }
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Você não tem permissão para acessar este inventário",
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
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<InventoryResponseDTO>> getInventory(@PathVariable UUID id) {
        InventoryResponseDTO inventory = inventoryService.getById(id);

        EntityModel<InventoryResponseDTO> resource = EntityModel.of(inventory,
                linkTo(methodOn(InventoryController.class).delete(id)).withRel("delete"));

        return ResponseEntity.ok(resource);
    }

    /**
     * Lista todos os inventários do usuário autenticado.
     *
     * @return Lista de inventários.
     */
    @Operation(
            summary = "Lista todos os inventários",
            description = "Retorna uma lista com todos os inventários do usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de inventários recuperada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    [
                                                      {
                                                        "id": "a1d2fb3a-dc03-4e2e-a09d-2a94f5670ed9",
                                                        "name": "Tintas acrílicas",
                                                        "description": "Inventário de tintas acrílicas",
                                                        "notificationEmail": "exemplo@email.com",
                                                        "ownerId": "bf42f203-aacb-43dd-a033-a05fd59267db"
                                                      },
                                                      {
                                                        "id": "18ced4b4-2789-41d5-8bd9-25986655db04",
                                                        "name": "Pincéis de cerdas naturais",
                                                        "description": "Inventário de pincéis de cerdas naturais",
                                                        "notificationEmail": "email@email",
                                                        "ownerId": "bf42f203-aacb-43dd-a033-a05fd59267db"
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
    public ResponseEntity<List<InventoryResponseDTO>> getAllInventories() {
        List<InventoryResponseDTO> inventories = inventoryService.getAll();

        return ResponseEntity.ok(inventories);
    }

    /**
     * Atualiza um inventário existente.
     *
     * @param id           Identificador do inventário a ser atualizado.
     * @param inventoryDTO Dados do inventário a ser atualizado.
     * @return Informações do inventário atualizado.
     */
    @Operation(
            summary = "Atualiza todos os campos de um inventário pelo ID",
            description = "Atualiza todos os campos de um inventário existente, incluindo nome, descrição e email de notificação."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Inventário atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                        "id": "a1d2fb3a-dc03-4e2e-a09d-2a94f5670ed9",
                                                        "name": "Tintas acrílicas - Atualizado",
                                                        "description": "Nova descrição do inventário",
                                                        "notificationEmail": "novo@email.com",
                                                        "ownerId": "bf42f203-aacb-43dd-a033-a05fd59267db",
                                                        "_links" : {
                                                            "self": {
                                                                "href": "http://localhost:8080/inventorize/v1/inventories/a1d2fb3a-dc03-4e2e-a09d-2a94f5670ed9"
                                                            },
                                                            "delete": {
                                                                "href": "http://localhost:8080/inventorize/v1/inventories/a1d2fb3a-dc03-4e2e-a09d-2a94f5670ed9"
                                                            }
                                                        }
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
                                                            "O nome do inventário é obrigatório nesse contexto",
                                                            "O nome do inventário deve ter no máximo 50 caracteres",
                                                            "A descrição do inventário deve ter no máximo 200 caracteres",
                                                            "A descrição do inventário é obrigatória neste contexto",
                                                            "O email para notificações é obrigatório neste contexto",
                                                            "O email para notificações deve ser válido"
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
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro ao atualizar o inventário",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                       "status": 500,
                                                       "message": "Erro ao atualizar",
                                                       "errorDetails": "O inventário não foi atualizado",
                                                       "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                     }"""
                                    )
                            }
                    )
            ),
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<InventoryResponseDTO>> putInventory(@PathVariable UUID id, @Valid @RequestBody UpdateInventoryDTO inventoryDTO) {
        InventoryResponseDTO updatedInventory = inventoryService.update(id, inventoryDTO);

        EntityModel<InventoryResponseDTO> resource = EntityModel.of(updatedInventory,
                linkTo(methodOn(InventoryController.class).getInventory(updatedInventory.id())).withSelfRel(),
                linkTo(methodOn(InventoryController.class).delete(updatedInventory.id())).withRel("delete"));

        return ResponseEntity.ok(resource);
    }

    /**
     * Atualiza parcialmente um inventário existente.
     *
     * @param id                  Identificador do inventário a ser atualizado.
     * @param inventoryRequestDTO Dados do inventário a ser atualizado.
     * @return Informações do inventário atualizado.
     */
    @Operation(
            summary = "Atualiza parcialmente um inventário existente",
            description = "Atualiza campos de um inventário existente, como nome, descrição e email de notificação."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Inventário atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "id": "a1d2fb3a-dc03-4e2e-a09d-2a94f5670ed9",
                                                      "name": "Novo nome",
                                                      "description": "Descrição atualizada",
                                                      "notificationEmail": "novo@email.com",
                                                      "ownerId": "bf42f203-aacb-43dd-a033-a05fd59267db",
                                                      "_links" : {
                                                        "self": {
                                                          "href": "http://localhost:8080/inventorize/v1/inventories/a1d2fb3a-dc03-4e2e-a09d-2a94f5670ed9"
                                                        },
                                                        "delete": {
                                                          "href": "http://localhost:8080/inventorize/v1/inventories/a1d2fb3a-dc03-4e2e-a09d-2a94f5670ed9"
                                                        }
                                                      }
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
                                                        "O nome do inventário deve ter no máximo 50 caracteres",
                                                        "A descrição do inventário deve ter no máximo 200 caracteres",
                                                        "O e-mail para notificações deve ser válido"
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
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro ao atualizar o inventário",
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
    public ResponseEntity<EntityModel<InventoryResponseDTO> > patchInventory(@PathVariable UUID id, @Valid @RequestBody PatchInventoryDTO inventoryRequestDTO) {
        InventoryResponseDTO updatedInventory = inventoryService.patch(id, inventoryRequestDTO);

        EntityModel<InventoryResponseDTO> resource = EntityModel.of(updatedInventory,
                linkTo(methodOn(InventoryController.class).getInventory(updatedInventory.id())).withRel("self"),
                linkTo(methodOn(InventoryController.class).delete(updatedInventory.id())).withRel("delete"));

        return ResponseEntity.ok(resource);
    }

    /**
     * Deleta um inventário pelo seu ID.
     *
     * @param id Identificador do inventário a ser deletado.
     * @return Mensagem de sucesso.
     */
    @Operation(
            summary = "Deleta um inventário pelo ID",
            description = "Remove um inventário específico pelo seu ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Inventário deletado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "message": "Inventário deletado com sucesso"
                                                    }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Você não tem permissão para deletar este inventário",
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
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        inventoryService.delete(id);

        return ResponseEntity.ok("{\n\"message\": \"Inventário deletado com sucesso\"");
    }
}