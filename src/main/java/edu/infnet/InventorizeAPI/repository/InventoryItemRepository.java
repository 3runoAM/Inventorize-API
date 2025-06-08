package edu.infnet.InventorizeAPI.repository;

import edu.infnet.InventorizeAPI.entities.Item;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryItemRepository extends JpaRepository<Item, UUID> {
    List<Item> getAllByInventoryId(UUID inventoryId);
    List<Item> getAllByInventoryIdIn(List<UUID> inventoryIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Item> findItemById(UUID inventoryItemId);
}