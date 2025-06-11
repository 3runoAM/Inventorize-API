package edu.infnet.inventorize.services;

import edu.infnet.inventorize.dto.request.product.PatchProductDTO;
import edu.infnet.inventorize.dto.request.product.ProductDTO;
import edu.infnet.inventorize.dto.request.product.UpdateProductDTO;
import edu.infnet.inventorize.dto.response.ProductResponseDTO;
import edu.infnet.inventorize.entities.AuthUser;
import edu.infnet.inventorize.entities.Product;
import edu.infnet.inventorize.exceptions.custom.ProductAlreadyExistsException;
import edu.infnet.inventorize.exceptions.custom.ProductNotFoundException;
import edu.infnet.inventorize.exceptions.custom.UnauthorizedRequestException;
import edu.infnet.inventorize.repository.ProductRepository;

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

    /**
     * Cria um novo produto.
     *
     * @param productData dados do produto a ser criado
     * @return informações do produto criado
     */
    public ProductResponseDTO createProduct(ProductDTO productData) {
        if (productRepository.existsByNameAndSupplierCode(productData.name(), productData.supplierCode())){
            throw new ProductAlreadyExistsException(String.format("Já existe um produto cadastrado com: [Nome: %s] e [Código de Fornecedor: %s]",
                    productData.name(), productData.supplierCode()));
        }

        var newProduct = Product.builder()
                .name(productData.name())
                .supplierCode(productData.supplierCode())
                .owner(authService.getAuthenticatedUser())
                .build();

        var savedProduct = productRepository.save(newProduct);

        return ProductResponseDTO.fromProduct(savedProduct);
    }

    /**
     * Busca um produto pelo seu ID.
     * @param id
     * @return
     */
    public ProductResponseDTO getById(UUID id) {
        var product = validateOwnershipById(id);

        return ProductResponseDTO.fromProduct(product);
    }

    /**
     * Busca todos os produtos do usuário autenticado.
     *
     * @return lista de produtos do usuário
     */
    public List<ProductResponseDTO> getAll() {
        var userInfo = authService.getAuthenticatedUser();

        return productRepository.findAllByOwnerId(userInfo.getId()).stream()
                .map(ProductResponseDTO::fromProduct)
                .toList();
    }

    /**
     * Deleta um produto pelo seu ID.
     *
     * @param id ID do produto a ser deletado
     */
    public void deleteById(UUID id) {
        var product = validateOwnershipById(id);

        productRepository.delete(product);
    }

    /**
     * Atualiza um produto existente com os dados fornecidos.
     *
     * @param productId ID do produto a ser atualizado
     * @param productData dados do produto a serem atualizados
     * @return informações do produto atualizado
     */
    @Transactional
    public ProductResponseDTO updateProduct(UUID productId, UpdateProductDTO productData) {
        Product product = validateOwnershipById(productId);

        var productBuilder = product.toBuilder()
                .id(productId)
                .name(productData.newName())
                .supplierCode(productData.newSupplierCode())
                .build();

        var updatedProduct = productRepository.save(productBuilder);

        return ProductResponseDTO.fromProduct(updatedProduct);
    }

    /**
     * Atualiza parcialmente um produto existente com os dados fornecidos.
     *
     * @param productId ID do produto a ser atualizado
     * @param productData dados do produto a serem atualizados
     * @return informações do produto atualizado
     */
    @Transactional
    public ProductResponseDTO patchProduct(UUID productId, PatchProductDTO productData) {
        Product product = validateOwnershipById(productId);
        var productBuilder = product.toBuilder();

        if (productData.name() != null) productBuilder.name(productData.name());
        if (productData.supplierCode() != null) productBuilder.supplierCode(productData.supplierCode());

        var updatedProduct = productRepository.save(productBuilder.build());

        return ProductResponseDTO.fromProduct(updatedProduct);
    }

    /**
     * Valida se o usuário autenticado é o proprietário do produto com o ID fornecido.
     *
     * @param productId ID do produto a ser validado
     * @return o produto se o usuário for o proprietário
     * @throws ProductNotFoundException se o produto não for encontrado
     * @throws UnauthorizedRequestException se o usuário não for o proprietário do produto
     */
    protected Product validateOwnershipById(UUID productId) {
        var product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(String.format("Produto com o [ ID: %s ] não encontrado", productId)));
        AuthUser currentUser = authService.getAuthenticatedUser();

        if (!product.getOwner().equals(currentUser)) throw new UnauthorizedRequestException("Usuário não tem autorização para gerenciar este produto");

        return product;
    }
}