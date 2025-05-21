package edu.infnet.InventorizeAPI.services;

import edu.infnet.InventorizeAPI.dto.request.ProductRequestDTO;
import edu.infnet.InventorizeAPI.dto.response.ProductResponseDTO;
import edu.infnet.InventorizeAPI.entities.Product;
import edu.infnet.InventorizeAPI.exceptions.custom.ProductAlreadyExistsException;
import edu.infnet.InventorizeAPI.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponseDTO createProduct(ProductRequestDTO productData) {
        if (productRepository.existsByNameAndSupplierCode(productData.name(), productData.supplierCode())){
            throw new ProductAlreadyExistsException("Já existe um produto cadastrado com: [Nome: %s] e [Código de Fornecedor: %s]"
                    .formatted(productData.name(), productData.supplierCode()));
        }

        var product = Product.builder()
                .name(productData.name())
                .supplierCode(productData.supplierCode());

        Product savedProduct = productRepository.save(product.build());

        return ProductResponseDTO.fromProduct(savedProduct);
    }
}