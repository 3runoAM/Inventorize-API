package edu.infnet.inventorize.controllers;

import edu.infnet.inventorize.dto.request.product.PatchProductDTO;
import edu.infnet.inventorize.dto.request.product.ProductDTO;
import edu.infnet.inventorize.dto.request.product.UpdateProductDTO;
import edu.infnet.inventorize.dto.response.ProductResponseDTO;
import edu.infnet.inventorize.services.ProductService;
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
@Tag(name = "Controller de Produtos", description = "Endpoints para gerenciamento de produtos")
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    /**
     * Cria um novo produto.
     *
     * @param productData dados do produto a ser criado
     * @return informações do produto criado
     */
    @Operation(
            summary = "Cria um novo Produto",
            description = "Cria um produto e retorna suas informações como nome e código do fornecedor"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Produto criado com sucesso",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "productId": "a0d7e008-e953-4b97-95a9-f73ece8d2f0d",
                                                      "ownerId": "8dbce360-0e12-4eb3-b9b6-eaaefdb34192",
                                                      "name": "Tinta Acrílica Dourada Pérola - 50ml",
                                                      "supplierCode": "CODIGO-001"
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
                                                            "O nome do produto é obrigatório",
                                                            "O nome do produto deve ter no máximo 100 caracteres",
                                                            "O código do fornecedor é obrigatório",
                                                            "O código do fornecedor deve ter no máximo 100 caracteres"
                                                        ],
                                                        "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                    }"""
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
                                                    {
                                                      "status": 500,
                                                      "message": "Ocorreu um erro inesperado",
                                                      "errorDetails": "Já existe um produto cadastrado com: [Nome: Nome do produto] e [Código de Fornecedor: Código de fornecedor]",
                                                      "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    public ResponseEntity<EntityModel<ProductResponseDTO> > createProduct(@Valid @RequestBody ProductDTO productData) {
        ProductResponseDTO savedProductInfo = productService.createProduct(productData);

        EntityModel<ProductResponseDTO> resource = EntityModel.of(savedProductInfo,
                linkTo(methodOn(ProductController.class).getById(savedProductInfo.productId())).withSelfRel(),
                linkTo(methodOn(ProductController.class).deleteById(savedProductInfo.productId())).withRel("deleteProduct"));

        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    /**
     * Busca um produto pelo seu ID.
     *
     * @param id identificador do produto
     * @return informações do produto encontrado
     */
    @Operation(
            summary = "Busca um produto pelo ID",
            description = "Retorna as informações do produto correspondente ao ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto recuperado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "productId": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                      "ownerId": "66e1e3c1-2548-4a68-9a02-0ac0bf62ae52",
                                                      "name": "Tinta Acrílica Dourada Pérola - 50ml",
                                                      "supplierCode": "CODIGO-001"
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
                    description = "Produto não encontrado",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                       "status": 404,
                                                       "message": "Produto não encontrado",
                                                       "errorDetails": "Produto com o [ ID: b947fb21-fa2d-4e41-93c5-b3f4cf425afb ] não encontrado",
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
    public ResponseEntity<EntityModel<ProductResponseDTO>> getById(@PathVariable UUID id) {
        ProductResponseDTO productInfo = productService.getById(id);

        EntityModel<ProductResponseDTO> resource = EntityModel.of(productInfo,
                linkTo(methodOn(ProductController.class).deleteById(productInfo.productId())).withRel("deleteProduct"));

        return ResponseEntity.ok(resource);
    }


    /**
     * Lista todos os produtos do usuário autenticado.
     *
     * @return lista de produtos
     */
    @Operation(
            summary = "Lista todos os Produtos",
            description = "Retorna todos os produtos do usuário autenticado"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de produtos recuperada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    [
                                                      {
                                                        "productId": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                        "ownerId": "66e1e3c1-2548-4a68-9a02-0ac0bf62ae52",
                                                        "name": "Tinta Acrílica Dourada Pérola - 50ml",
                                                        "supplierCode": "CODIGO-001"
                                                      },
                                                      {
                                                        "productId": "b1234567-89ab-cdef-0123-456789abcdef",
                                                        "ownerId": "66e1e3c1-2548-4a68-9a02-0ac0bf62ae52",
                                                        "name": "Pincel Chato 12mm",
                                                        "supplierCode": "CODIGO-002"
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
    public ResponseEntity<List<ProductResponseDTO>> getAll() {
        List<ProductResponseDTO> productList = productService.getAll();

        return ResponseEntity.ok(productList);
    }

    /**
     * Atualiza um produto existente.
     *
     * @param id          identificador do produto a ser atualizado
     * @param productData dados do produto a ser atualizado
     * @return informações do produto atualizado
     */
    @Operation(
            summary = "Atualiza todos os campos de um produto pelo ID",
            description = "Atualiza o nome e o código de fornecedor pelo seu ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                      {
                                                        "productId": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                        "ownerId": "66e1e3c1-2548-4a68-9a02-0ac0bf62ae52",
                                                        "name": "Tinta Acrílica Dourada Pérola - 50ml",
                                                        "supplierCode": "CODIGO-001"
                                                      },
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
                                                            "O novo código de fornecedor é obrigatório nesse contexto",
                                                            "O novo código de fornecedor deve ter 100 caracteres no máximo",
                                                            "O novo nome deve ter 100 caracteres no máximo",
                                                            "O novo nome é obrigatório nesse contexto"
                                                        ],
                                                        "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                    }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Você não tem permissão para atualizar este produto",
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
                    description = "Produto não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                       "status": 404,
                                                       "message": "Produto não encontrado",
                                                       "errorDetails": "Produto com o [ ID: b947fb21-fa2d-4e41-93c5-b3f4cf425afb ] não encontrado",
                                                       "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                     }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro ao atualizar produto",
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
    public ResponseEntity<EntityModel<ProductResponseDTO>> updateProduct(@PathVariable UUID id, @Valid @RequestBody UpdateProductDTO productData) {
        var productResponseDTO = productService.updateProduct(id, productData);

        EntityModel<ProductResponseDTO> resource = EntityModel.of(productResponseDTO,
                linkTo(methodOn(ProductController.class).getById(productResponseDTO.productId())).withSelfRel(),
                linkTo(methodOn(ProductController.class).deleteById(productResponseDTO.productId())).withRel("deleteProduct"));

        return ResponseEntity.ok(resource);
    }

    /**
     * Atualiza parcialmente um produto existente.
     *
     * @param id          identificador do produto a ser atualizado
     * @param productData dados do produto a ser atualizado
     * @return informações do produto atualizado
     */
    @Operation(
            summary = "Atualiza parcialmente um produto existente",
            description = "Atualiza campos de um produto existente pelo ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "productId": "a015aebc-5388-4aac-9037-21aff2c65390",
                                                      "ownerId": "66e1e3c1-2548-4a68-9a02-0ac0bf62ae52",
                                                      "name": "Tinta Acrílica - NOVO",
                                                      "supplierCode": "N0V0C0D1"
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
                                                          "O nome precisa ter no mínimo 2 e no máximo 100 caracteres",
                                                           "O código de fornecedor precisa ter no mínimo 3 e no máximo 100 caracteres"
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
                    description = "Produto não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                       "status": 404,
                                                       "message": "Produto não encontrado",
                                                       "errorDetails": "Produto com o [ ID: b947fb21-fa2d-4e41-93c5-b3f4cf425afb ] não encontrado",
                                                       "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                     }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro ao atualizar o produto",
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
    public ResponseEntity<EntityModel<ProductResponseDTO>> patchProduct(@PathVariable UUID id, @Valid @RequestBody PatchProductDTO productData) {
        var productResponseDTO = productService.patchProduct(id, productData);

        EntityModel<ProductResponseDTO> resource = EntityModel.of(productResponseDTO,
                linkTo(methodOn(ProductController.class).getById(productResponseDTO.productId())).withSelfRel(),
                linkTo(methodOn(ProductController.class).deleteById(productResponseDTO.productId())).withRel("deleteProduct"));

        return ResponseEntity.ok(resource);
    }

    /**
     * Deleta um produto pelo seu ID.
     *
     * @param id identificador do produto a ser deletado
     * @return mensagem de sucesso
     */
    @Operation(
            summary = "Deleta um produto pelo ID",
            description = "Remove um produto específico pelo seu ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto deletado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "message": "Produto deletado com sucesso"
                                                    }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Você não tem permissão para deletar este produto",
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
                    description = "Erro ao deletar o produto",
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
    public ResponseEntity<String> deleteById(@PathVariable UUID id) {
        productService.deleteById(id);

        return ResponseEntity.ok("Produto deletado com sucesso");
    }
}