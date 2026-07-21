package com.jashawn.inventory_api.stockItem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StockItemRepository extends JpaRepository<StockItem, UUID> {

    Optional<StockItem> findByProductIdAndWarehouseId(UUID productId, UUID warehouseId);
}
