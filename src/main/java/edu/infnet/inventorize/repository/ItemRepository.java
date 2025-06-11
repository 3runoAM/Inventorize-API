package edu.infnet.inventorize.repository;

import edu.infnet.inventorize.entities.Item;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {
    List<Item> getAllItemsByInventoryId(UUID inventoryId);
    List<Item> getAllItemsByInventoryIdIn(List<UUID> inventoryIds);

    @Query("SELECT i FROM Item i WHERE i.inventory.id IN :inventoryIds AND i.currentQuantity < i.minimumStockLevel")
    List<Item> findLowStockItemsByInventoryIdIn(@Param("inventoryIds") List<UUID> inventoryIds);

    List<Item> getAllWhereMinimumStockLevelIsLowerThanCurrentQuantityByInventoryIdIn(List<UUID> inventoryIds);

    Optional<Item> findById(UUID itemId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Item> findItemById(UUID inventoryItemId);
}