package edu.infnet.InventorizeAPI.repositories.map;

import edu.infnet.InventorizeAPI.entities.Product;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ProductMapRepository {
    private final Map<UUID,Product> productMap;

    public ProductMapRepository() {
        this.productMap = new HashMap<>();
    }

    public void save(Product product) {
        productMap.put(product.getId(), product);
    }

    public List<Product> findAll() {
        return new ArrayList<>(productMap.values());
    }
}