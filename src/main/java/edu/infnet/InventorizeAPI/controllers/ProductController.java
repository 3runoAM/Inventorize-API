package edu.infnet.InventorizeAPI.controllers;

import edu.infnet.InventorizeAPI.dto.request.ProductRequestDTO;
import edu.infnet.InventorizeAPI.dto.response.ProductResponseDTO;
import edu.infnet.InventorizeAPI.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // post
    @PostMapping("/new")
    public ResponseEntity<ProductResponseDTO> createNewProduct(@Valid @RequestBody ProductRequestDTO productData) {
        ProductResponseDTO savedProductInfo = productService.createProduct(productData);
        return ResponseEntity.ok(savedProductInfo);
    }

    // get


    // put

    // patch

}