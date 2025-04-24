package edu.infnet.InventorizeAPI.tests;

import edu.infnet.InventorizeAPI.entities.Product;
import edu.infnet.InventorizeAPI.services.ProductService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Component
public class ProductTest implements ApplicationRunner {
    private final ProductService productService;

    public ProductTest(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Files.readAllLines(Path.of("src/main/resources/csv/products_data.csv"))
                .forEach(line -> {
                            String[] data = line.split(",");
                            Product product = productService.processProductData(data);
                            productService.saveProduct(product);
                        }
                );
        System.out.println("#PRODUTOS");
        productService.getAllProducts().forEach(System.out::println);
    }
}