package edu.infnet.InventorizeAPI.services;

import edu.infnet.InventorizeAPI.dto.request.product.PatchProductDTO;
import edu.infnet.InventorizeAPI.dto.request.product.ProductDTO;
import edu.infnet.InventorizeAPI.dto.request.product.UpdateProductDTO;
import edu.infnet.InventorizeAPI.dto.response.ProductResponseDTO;
import edu.infnet.InventorizeAPI.entities.AuthUser;
import edu.infnet.InventorizeAPI.entities.Product;
import edu.infnet.InventorizeAPI.enums.Role;
import edu.infnet.InventorizeAPI.exceptions.custom.ProductAlreadyExistsException;
import edu.infnet.InventorizeAPI.exceptions.custom.ProductNotFoundException;
import edu.infnet.InventorizeAPI.exceptions.custom.UnauthorizedRequestException;
import edu.infnet.InventorizeAPI.repository.ProductRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//@Disabled
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private ProductService productService;


    // TESTES DE CRIAÇÃO DE PRODUTO -----------------------------------------------------------------------------------
    @Test
    public void shouldReturnCorrectProductDataWhenCreating() {
        var productRequestDto = mockedProductRequest();
        var mockedProduct = mockedProduct();
        var mockedUser = mockedUser();
        var authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(productRepository.existsByNameAndSupplierCode(productRequestDto.name(), productRequestDto.supplierCode())).thenReturn(false);
        when(authenticationService.getAuthenticatedUser()).thenReturn(mockedUser);
        when(productRepository.save(any(Product.class))).thenReturn(mockedProduct);

        var productResponseDto = productService.createProduct(productRequestDto);

        assertEquals(productResponseDto.productId(), mockedProduct.getId(), "O id do produto retornado deve permanecer o mesmo");
        assertEquals(productResponseDto.name(), mockedProduct.getName(), "O nome do produto retornado deve ser igual ao enviado na requisição");
        assertEquals(productResponseDto.supplierCode(), mockedProduct.getSupplierCode(), "O código de fornecedor do produto retornado deve ser igual ao enviado na requisição");
    }

    @Test
    public void savedProductShouldHaveRightData() {
        var productRequestDto = mockedProductRequest();

        var mockedProduct = mockedProduct();
        var mockedUser = mockedUser();
        var authentication = mock(Authentication.class);

        var productCaptor = getProductCaptor();

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(productRepository.existsByNameAndSupplierCode(productRequestDto.name(), productRequestDto.supplierCode())).thenReturn(false);
        when(authenticationService.getAuthenticatedUser()).thenReturn(mockedUser);
        when(productRepository.save(any(Product.class))).thenReturn(mockedProduct);

        productService.createProduct(productRequestDto);

        verify(productRepository).save(productCaptor.capture());
        Product capturedProduct = productCaptor.getValue();

        assertEquals(capturedProduct.getName(), productRequestDto.name(), "O nome do produto salvo deve ser igual ao enviado na requisição");
        assertEquals(capturedProduct.getSupplierCode(), productRequestDto.supplierCode(), "O código de fornecedor do produto salvo deve ser igual ao enviado na requisição");
        assertEquals(capturedProduct.getOwner().getId(), mockedUser.getId(), "O dono do produto salvo deve ser o usuário autenticado");
    }

    @Test
    public void shouldThrowExceptionWhenProductAlreadyExists() {
        var productRequestDto = mockedProductRequest();

        when(productRepository.existsByNameAndSupplierCode(productRequestDto.name(), productRequestDto.supplierCode())).thenReturn(true);

        var ProductAlreadyExistsException = assertThrows(ProductAlreadyExistsException.class, () -> productService.createProduct(productRequestDto));
        assertEquals(
                String.format("Já existe um produto cadastrado com: [Nome: %s] e [Código de Fornecedor: %s]", productRequestDto.name(), productRequestDto.supplierCode()),
                ProductAlreadyExistsException.getMessage()
        );
    }

    @Test
    public void shouldCallCorrectMethodsWhenCreatingProducts() {
        var productRequestDto = mockedProductRequest();
        var mockedProduct = mockedProduct();
        var mockedUser = mockedUser();
        var authentication = mock(Authentication.class);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(productRepository.existsByNameAndSupplierCode(productRequestDto.name(), productRequestDto.supplierCode())).thenReturn(false);
        when(authenticationService.getAuthenticatedUser()).thenReturn(mockedUser);
        when(productRepository.save(any(Product.class))).thenReturn(mockedProduct);

        productService.createProduct(productRequestDto);

        verify(productRepository).existsByNameAndSupplierCode(productRequestDto.name(), productRequestDto.supplierCode());
        verify(authenticationService).getAuthenticatedUser();
        verify(productRepository).save(any(Product.class));
        verifyNoMoreInteractions(productRepository, authenticationService);
    }

    // TESTES DE BUSCA E VALIDAÇÃO DE PRODUTO --------------------------------------------------------------------------
    @Test
    public void shouldValidateOwnershipAndReturnProductCorrectly() {
        var product = mockedProduct();
        var user = mockedUser();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);

        var validatedProduct = productService.validateOwnershipById(UUID.fromString("7e8ddac6-26fe-40a2-afb0-44892787775b"));

        assertEquals(product.getId(), validatedProduct.getId(), "O ID do produto validado deve ser igual ao ID do produto mockado");
        assertEquals(product.getName(), validatedProduct.getName(), "O nome do produto validado deve ser igual ao nome do produto mockado");
        assertEquals(product.getSupplierCode(), validatedProduct.getSupplierCode(), "O código de fornecedor do produto validado deve ser igual ao código do produto mockado");
    }

    @Test
    public void shouldThrowExceptionWhenProductNotFound() {
        var productId = mockedProduct().getId();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        var productNotFoundException = assertThrows(ProductNotFoundException.class, () -> productService.validateOwnershipById(productId));
        assertEquals(
                String.format("Produto com o [ ID: %s ] não encontrado", productId),
                productNotFoundException.getMessage()
        );
    }

    @Test
    public void shouldThrowExceptionWhenProductNotOwnedByUser() {
        var product = mockedProduct();
        var unauthorizedUser = AuthUser.builder()
                .id(UUID.fromString("f73f4660-5a70-45a9-8da2-6d61a664dbef"))
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(authenticationService.getAuthenticatedUser()).thenReturn(unauthorizedUser);

        assertThrows(UnauthorizedRequestException.class, () -> productService.validateOwnershipById(product.getId()));
    }

    @Test
    public void shouldCallCorrectMethodsWhenValidatingOwnership() {
        var product = mockedProduct();
        var user = mockedUser();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);

        productService.validateOwnershipById(product.getId());

        verify(productRepository).findById(product.getId());
        verify(authenticationService).getAuthenticatedUser();
        verifyNoMoreInteractions(productRepository, authenticationService);
    }

    // TESTE DE BUSCA POR ID ------------------------------------------------------------------------------------------
    @Test
    public void shouldReturnCorrectProductById() {
        var product = mockedProduct();
        var user = mockedUser();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);

        var productResponseDTO = productService.getById(product.getId());

        assertEquals(productResponseDTO.productId(), product.getId(), "O ID do produto encontrado deve ser igual ao do produto mockado");
        assertEquals(productResponseDTO.name(), product.getName(), "O nome do produto encontrado eve ser igual ao do produto mockado");
        assertEquals(productResponseDTO.supplierCode(), product.getSupplierCode(), "O código de fornecedor do produto encontrado deve ser igual ao do produto mockado");
    }

    // TESTE DE RECUPERAR TODOS OS PRODUTOS ----------------------------------------------------------------------------
    @Test
    public void shouldReturnAllProductsForAuthenticatedUser() {
        var user = mockedUser();
        var mockedProductList = mockedProductList();

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(productRepository.findAllByOwnerId(user.getId())).thenReturn(mockedProductList);


        List<ProductResponseDTO> productResponseDTOList = productService.getAll();

        assertEquals(productResponseDTOList.size(), mockedProductList.size(), "O tamanho da lista de produtos retornada deve ser igual ao tamanho da lista mockada");
        for (int i = 0; i < mockedProductList.size(); i++) {
            assertEquals(
                    productResponseDTOList.get(i),
                    ProductResponseDTO.fromProduct(mockedProductList.get(i)),
                    "O produto retornado na posição " + i + " deve ser igual ao produto mockado na mesma posição"
            );
        }
    }

    @Test
    public void shouldCallCorrectMethodsWhenGettingAllProducts() {
        var user = mockedUser();
        var mockedProductList = mockedProductList();

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(productRepository.findAllByOwnerId(user.getId())).thenReturn(mockedProductList);

        productService.getAll();

        verify(authenticationService).getAuthenticatedUser();
        verify(productRepository).findAllByOwnerId(user.getId());
        verifyNoMoreInteractions(authenticationService, productRepository);
    }

    // TESTE DE DELEÇÃO DE PRODUTO -------------------------------------------------------------------------------------
    @Test
    public void shouldCallDeletionMethod() {
        var product = mockedProduct();
        var user = mockedUser();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);

        productService.deleteById(product.getId());

        verify(productRepository).findById(product.getId());
        verify(authenticationService).getAuthenticatedUser();
        verify(productRepository).delete(product);
    }

    // TESTES DE ATUALIZAÇÃO DE PRODUTO -------------------------------------------------------------------------------
    @Test
    public void shouldReturnCorrectResponseToUpdateProduct() {
        var putProductDTO = mockedPutProductDTO();
        var mockedProduct = mockedProduct();
        var user = mockedUser();
        var updatedProduct = mockedProduct.toBuilder()
                .name(putProductDTO.newName())
                .supplierCode(putProductDTO.newSupplierCode())
                .build();

        when(productRepository.findById(mockedProduct.getId())).thenReturn(Optional.of(mockedProduct));
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        var productResponseDTO = productService.updateProduct(mockedProduct.getId(), putProductDTO);

        assertEquals(productResponseDTO.productId(), mockedProduct.getId());
        assertEquals(productResponseDTO.ownerId(), user.getId());
        assertEquals(productResponseDTO.name(), updatedProduct.getName());
        assertEquals(productResponseDTO.supplierCode(), updatedProduct.getSupplierCode());
    }

    @Test
    public void savedProductShouldHaveCorrectData() {
        var putProductDTO = mockedPutProductDTO();
        var mockedProduct = mockedProduct();
        var user = mockedUser();
        var updatedProduct = mockedProduct.toBuilder()
                .name(putProductDTO.newName())
                .supplierCode(putProductDTO.newSupplierCode())
                .build();
        var productCaptor = getProductCaptor();

        when(productRepository.findById(mockedProduct.getId())).thenReturn(Optional.of(mockedProduct));
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        productService.updateProduct(mockedProduct.getId(), putProductDTO);

        verify(productRepository).save(productCaptor.capture());
        var savedProduct = productCaptor.getValue();

        assertEquals(savedProduct.getId(), mockedProduct.getId());
        assertEquals(savedProduct.getOwner().getId(), user.getId());
        assertEquals(savedProduct.getName(), updatedProduct.getName());
        assertEquals(savedProduct.getSupplierCode(), updatedProduct.getSupplierCode());
    }

    @Test
    public void shouldCallCorrectMethodsToUpdateProduct() {
        var putProductDTO = mockedPutProductDTO();
        var mockedProduct = mockedProduct();
        var user = mockedUser();
        var updatedProduct = mockedProduct.toBuilder()
                .name(putProductDTO.newName())
                .supplierCode(putProductDTO.newSupplierCode())
                .build();

        when(productRepository.findById(mockedProduct.getId())).thenReturn(Optional.of(mockedProduct));
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        productService.updateProduct(mockedProduct.getId(), putProductDTO);

        verify(productRepository).findById(mockedProduct.getId());
        verify(authenticationService).getAuthenticatedUser();
        verify(productRepository).save(any(Product.class));
        verifyNoMoreInteractions(productRepository, authenticationService);
    }

    @Test
    public void shouldPatchNameCorrectly() {
        var mockedProduct = mockedProduct();
        var user = mockedUser();

        var newName = "ProductABC";
        var patchNameDTO = new PatchProductDTO(newName, null);
        var updatedProduct = mockedProduct.toBuilder()
                .name(newName)
                .build();

        var captor = getProductCaptor();

        when(productRepository.findById(mockedProduct.getId())).thenReturn(Optional.of(mockedProduct));
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        productService.patchProduct(mockedProduct.getId(), patchNameDTO);

        verify(productRepository).save(captor.capture());
        var savedProduct = captor.getValue();

        assertEquals(mockedProduct.getId(), savedProduct.getId(), "O ID do produto salvo deve ser o mesmo do produto mockado");
        assertEquals(user.getId(), savedProduct.getOwner().getId(), "O dono do produto salvo deve ser o usuário autenticado");
        assertEquals(newName, savedProduct.getName(), "O nome do produto salvo deve ser atualizado corretamente");
        assertEquals(savedProduct.getSupplierCode(), mockedProduct.getSupplierCode(), "O código de fornecedor do produto salvo deve permanecer o mesmo");
    }

    @Test
    public void shouldPatchSupplierCodeCorrectly() {
        var product = mockedProduct();
        var user = mockedUser();

        var newSupplierCode = "URB123";
        var updatedProduct = product.toBuilder()
                .supplierCode(newSupplierCode)
                .build();

        var captor = getProductCaptor();

        var patchSupplierCodeDTO = new PatchProductDTO(null, newSupplierCode);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        var productResponseDTO = productService.patchProduct(product.getId(), patchSupplierCodeDTO);

        verify(productRepository).save(captor.capture());
        var savedProduct = captor.getValue();

        assertEquals(product.getId(), savedProduct.getId(), "O ID do produto salvo deve ser o mesmo do produto mockado");
        assertEquals(user.getId(), savedProduct.getOwner().getId(), "O dono do produto salvo deve ser o usuário autenticado");
        assertEquals(newSupplierCode, savedProduct.getSupplierCode(), "O código de fornecedor do produto salvo deve ser atualizado corretamente");
        assertEquals(savedProduct.getName(), product.getName(), "O nome do produto salvo deve permanecer o mesmo");
    }

    @Test
    public void shouldCallCorrectMethodsToPatchProduct() {
        var product = mockedProduct();
        var user = mockedUser();
        var patchProductDTO = new PatchProductDTO("NovoNome", "NovoFornecedor");
        var updatedProduct = product.toBuilder()
                .name(patchProductDTO.name())
                .supplierCode(patchProductDTO.supplierCode())
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        productService.patchProduct(product.getId(), patchProductDTO);

        verify(productRepository).findById(product.getId());
        verify(authenticationService).getAuthenticatedUser();
        verify(productRepository).save(any(Product.class));
        verifyNoMoreInteractions(productRepository, authenticationService);
    }

    // MÉTODOS UTILITÁRIOS ---------------------------------------------------------------------------------------------

    private ProductDTO mockedProductRequest() {
        return new ProductDTO("ProductB", "BRU123");
    }

    private Product mockedProduct() {
        return Product.builder()
                .id(UUID.fromString("7e8ddac6-26fe-40a2-afb0-44892787775b"))
                .name("ProductB")
                .supplierCode("BRU123")
                .owner(mockedUser())
                .build();
    }

    private AuthUser mockedUser() {
        return AuthUser.builder()
                .id(UUID.fromString("7e8ddac6-26fe-40a2-afb0-44892787775b"))
                .email("teste.teste@teste.com")
                .hashPassword("8ef1ae0e8d32f1e02bb4a5eb06c5f0cc")
                .roles(Set.of(Role.ROLE_USER))
                .build();
    }

    private ArgumentCaptor<Product> getProductCaptor() {
        return ArgumentCaptor.forClass(Product.class);
    }

    private List<Product> mockedProductList() {
        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            productList.add(
                    Product.builder().id(UUID.randomUUID())
                            .owner(mockedUser())
                            .name("Product" + i)
                            .supplierCode("BRU123" + i)
                            .build()
            );
        }
        return productList;
    }

    private UpdateProductDTO mockedPutProductDTO() {
        return new UpdateProductDTO("UpdatedProduct", "BRU12345");
    }
}
