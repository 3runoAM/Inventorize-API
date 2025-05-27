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

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO productData) {
        ProductResponseDTO savedProductInfo = productService.createProduct(productData);

        return ResponseEntity.ok(savedProductInfo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable UUID id) {
        ProductResponseDTO productInfo = productService.getById(id);

        return ResponseEntity.ok(productInfo);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAll() {
        List<ProductResponseDTO> productList = productService.getAll();

        return ResponseEntity.ok(productList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable UUID id) {
        productService.deleteById(id);

        return ResponseEntity.ok("Produto deletado com sucesso");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable UUID id, @Valid @RequestBody ProductRequestDTO productData) {
        var productResponseDTO = productService.putProduct(id, productData);

        return ResponseEntity.ok(productResponseDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> patchProduct(@PathVariable UUID id, @Valid @RequestBody ProductRequestDTO productData) {
        var productResponseDTO = productService.patchProduct(id, productData);

        return ResponseEntity.ok(productResponseDTO);
    }
}