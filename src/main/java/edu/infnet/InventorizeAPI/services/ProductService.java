package edu.infnet.InventorizeAPI.services;

import edu.infnet.InventorizeAPI.dto.request.ProductRequestDTO;
import edu.infnet.InventorizeAPI.dto.response.ProductResponseDTO;
import edu.infnet.InventorizeAPI.entities.Product;
import edu.infnet.InventorizeAPI.exceptions.custom.DeletingEntityException;
import edu.infnet.InventorizeAPI.exceptions.custom.ProductAlreadyExistsException;
import edu.infnet.InventorizeAPI.exceptions.custom.ProductNotFoundException;
import edu.infnet.InventorizeAPI.exceptions.custom.UnauthorizedRequestException;
import edu.infnet.InventorizeAPI.repository.ProductRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final AuthenticationService authService;
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository, AuthenticationService authService) {
        this.productRepository = productRepository;
        this.authService = authService;
    }

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

    public ProductResponseDTO getById(UUID id) {
        var product = validateProductOwnership(id);

        return ProductResponseDTO.fromProduct(product);
    }

    public List<ProductResponseDTO> getAll() {
        var userInfo = authService.getAuthenticatedUserInfo();

        return productRepository.findAllByOwnerId(userInfo.userId()).stream()
                .map(ProductResponseDTO::fromProduct)
                .toList();
    }

    public void deleteById(UUID id) {
        var product = validateProductOwnership(id);

        productRepository.delete(product);

        if(productRepository.existsById(id)) throw new DeletingEntityException(String.format("Erro ao deletar o produto com o [ ID: %s ]", id));
    }

    @Transactional
    public ProductResponseDTO updateProduct(UUID productId, ProductRequestDTO productData) {
        var product = validateProductOwnership(productId);
        var productBuilder = product.toBuilder();

        if (productData.name() != null) productBuilder.name(productData.name());
        if (productData.supplierCode() != null) productBuilder.supplierCode(productData.supplierCode());

        var updatedProduct = productRepository.save(productBuilder.build());

        return ProductResponseDTO.fromProduct(updatedProduct);
    }

    // Métodos utilitários

    private Product validateProductOwnership(UUID productId) {
        var userInfo = authService.getAuthenticatedUserInfo();
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(String.format("Produto com o [ ID: %s ] não encontrado", productId)));

        if (!product.getOwner().getId().equals(userInfo.userId())) throw new UnauthorizedRequestException("Você não tem permissão para gerenciar este produto.");

        return product;
    }
}