package edu.infnet.InventorizeAPI.services;

import edu.infnet.InventorizeAPI.entities.Product;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductService {
    private final Map<UUID, Product> productMap;

    public ProductService() {
        this.productMap = new HashMap<>();
    }

    public Product processProductData(String[] data) {
        return Product.builder()
                .id(UUID.fromString(data[0]))
                .name(data[1])
                .supplierCode(data[2])
                .build();
    }

    public void saveProduct(Product product) {
        productMap.put(product.getId(), product);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(productMap.values());
    }
}