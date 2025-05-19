package edu.infnet.InventorizeAPI.services;

import edu.infnet.InventorizeAPI.entities.Product;
import edu.infnet.InventorizeAPI.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
}