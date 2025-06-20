package edu.infnet.inventorize.repository;

import edu.infnet.inventorize.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsByNameAndSupplierCode(String name, String supplierCode);

    List<Product> findAllByOwnerId(UUID ownerId);
}