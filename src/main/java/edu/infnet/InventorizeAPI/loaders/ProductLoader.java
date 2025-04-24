package edu.infnet.InventorizeAPI.loaders;

import edu.infnet.InventorizeAPI.services.ProductService;
import jakarta.annotation.PostConstruct;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Order(1)
public class ProductLoader {
    private final ProductService productService;

    public ProductLoader(ProductService productService) {
        this.productService = productService;
    }

    @PostConstruct
    public void loadData() throws IOException {
        Files.readAllLines(Path.of("src/main/resources/csv/products_data.csv"))
            .forEach(line -> {;
                String[] data = line.split(",");
                productService.processProductData(data);
            }
        );
    }
}