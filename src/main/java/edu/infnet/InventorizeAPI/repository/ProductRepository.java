package edu.infnet.InventorizeAPI.repository;

import edu.infnet.InventorizeAPI.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsByNameAndSupplierCode(String name, String supplierCode);
}