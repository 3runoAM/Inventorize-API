package edu.infnet.InventorizeAPI.services;

import edu.infnet.InventorizeAPI.entities.Product;
import edu.infnet.InventorizeAPI.repositories.map.ProductMapRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductMapRepository productMapRepository;

    public ProductService(ProductMapRepository productMapRepository) {
        this.productMapRepository = productMapRepository;
    }

    public void processProductData(String[] data) {
        Product product = Product.builder()
                .id(UUID.fromString(data[0]))
                .name(data[1])
                .supplierCode(data[2])
                .build();

        productMapRepository.save(product);
    }

    public void saveProduct(Product product) {
        productMapRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productMapRepository.findAll();
    }
}