package edu.infnet.InventorizeAPI.repository;

import edu.infnet.InventorizeAPI.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<UUID, Inventory> {

}