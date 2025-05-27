package edu.infnet.InventorizeAPI.services;

import edu.infnet.InventorizeAPI.dto.request.ProductRequestDTO;
import edu.infnet.InventorizeAPI.dto.response.ProductResponseDTO;
import edu.infnet.InventorizeAPI.entities.AuthUser;
import edu.infnet.InventorizeAPI.entities.Product;
import edu.infnet.InventorizeAPI.exceptions.custom.ProductAlreadyExistsException;
import edu.infnet.InventorizeAPI.exceptions.custom.ProductNotFoundException;
import edu.infnet.InventorizeAPI.exceptions.custom.UnauthorizedRequestException;
import edu.infnet.InventorizeAPI.repository.ProductRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final AuthenticationService authService;
    private final ProductRepository productRepository;

    // POST
    public ProductResponseDTO createProduct(ProductRequestDTO productData) {
        if (productRepository.existsByNameAndSupplierCode(productData.name(), productData.supplierCode())){
            throw new ProductAlreadyExistsException(String.format("Já existe um produto cadastrado com: [Nome: %s] e [Código de Fornecedor: %s]",
                    productData.name(), productData.supplierCode()));
        }

        var product = Product.builder()
                .name(productData.name())
                .supplierCode(productData.supplierCode());

        var savedProduct = productRepository.save(product.build());

        return ProductResponseDTO.fromProduct(savedProduct);
    }

    // GET ONE
    public ProductResponseDTO getById(UUID id) {
        var product = validateOwnershipById(id);

        return ProductResponseDTO.fromProduct(product);
    }

    // GET ALL
    public List<ProductResponseDTO> getAll() {
        var userInfo = authService.getAuthenticatedUser();

        return productRepository.findAllByOwnerId(userInfo.getId()).stream()
                .map(ProductResponseDTO::fromProduct)
                .toList();
    }

    // DELETE
    public void deleteById(UUID id) {
        var product = validateOwnershipById(id);

        productRepository.delete(product);
    }

    @Transactional
    public ProductResponseDTO putProduct(UUID productId, ProductRequestDTO productData) {
        Product product = validateOwnershipById(productId);

        var productBuilder = product.toBuilder()
                .id(productId)
                .name(productData.name())
                .supplierCode(productData.supplierCode())
                .build();

        var updatedProduct = productRepository.save(productBuilder);

        return ProductResponseDTO.fromProduct(updatedProduct);
    }

    @Transactional
    public ProductResponseDTO patchProduct(UUID productId, ProductRequestDTO productData) {
        Product product = validateOwnershipById(productId);
        var productBuilder = product.toBuilder();

        if (productData.name() != null) productBuilder.name(productData.name());
        if (productData.supplierCode() != null) productBuilder.supplierCode(productData.supplierCode());

        var updatedProduct = productRepository.save(productBuilder.build());

        return ProductResponseDTO.fromProduct(updatedProduct);
    }



    // Métodos utilitários
    private Product validateOwnershipById(UUID productId) {
        var product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(String.format("Produto com o [ ID: %s ] não encontrado", productId)));
        AuthUser currentUser = authService.getAuthenticatedUser();

        if (!product.getOwner().getId().equals(currentUser.getId())) throw new UnauthorizedRequestException("Usuário não tem autorização para gerenciar este produto");

        return product;
    }
}