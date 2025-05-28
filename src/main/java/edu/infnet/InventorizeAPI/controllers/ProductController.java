package edu.infnet.InventorizeAPI.controllers;

import edu.infnet.InventorizeAPI.dto.request.ProductRequestDTO;
import edu.infnet.InventorizeAPI.dto.response.ProductResponseDTO;
import edu.infnet.InventorizeAPI.services.ProductService;
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
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    /**
     * Cria um novo produto.
     *
     * @param productData dados do produto a ser criado
     * @return informações do produto criado
     */
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO productData) {
        ProductResponseDTO savedProductInfo = productService.createProduct(productData);

        return ResponseEntity.ok(savedProductInfo);
    }

    /**
     * Busca um produto pelo seu ID.
     *
     * @param id identificador do produto
     * @return informações do produto encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable UUID id) {
        ProductResponseDTO productInfo = productService.getById(id);

        return ResponseEntity.ok(productInfo);
    }


    /**
     * Lista todos os produtos do usuário autenticado.
     *
     * @return lista de produtos
     */
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAll() {
        List<ProductResponseDTO> productList = productService.getAll();

        return ResponseEntity.ok(productList);
    }

    /**
     * Deleta um produto pelo seu ID.
     *
     * @param id identificador do produto a ser deletado
     * @return mensagem de sucesso
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable UUID id) {
        productService.deleteById(id);

        return ResponseEntity.ok("Produto deletado com sucesso");
    }

    /**
     * Atualiza um produto existente.
     *
     * @param id identificador do produto a ser atualizado
     * @param productData dados do produto a ser atualizado
     * @return informações do produto atualizado
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable UUID id, @Valid @RequestBody ProductRequestDTO productData) {
        var productResponseDTO = productService.putProduct(id, productData);

        return ResponseEntity.ok(productResponseDTO);
    }

    /**
     * Atualiza parcialmente um produto existente.
     *
     * @param id identificador do produto a ser atualizado
     * @param productData dados do produto a ser atualizado
     * @return informações do produto atualizado
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> patchProduct(@PathVariable UUID id, @Valid @RequestBody ProductRequestDTO productData) {
        var productResponseDTO = productService.patchProduct(id, productData);

        return ResponseEntity.ok(productResponseDTO);
    }
}