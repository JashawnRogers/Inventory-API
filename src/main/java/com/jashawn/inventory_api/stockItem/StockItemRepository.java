package com.jashawn.inventory_api.stockItem;

import com.jashawn.inventory_api.inventory.projection.InventoryValue;
import com.jashawn.inventory_api.inventory.projection.InventoryValueByWarehouse;
import com.jashawn.inventory_api.stockItem.dto.StockAvailability;
import com.jashawn.inventory_api.stockItem.dto.StockAvailabilityByWarehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface StockItemRepository extends JpaRepository<StockItem, UUID> {

    Optional<StockItem> findByProductIdAndWarehouseId(UUID productId, UUID warehouseId);

    @Query("SELECT s FROM StockItem s WHERE s.createdAt BETWEEN :startDate AND :endDate ")
    Page<StockItem> getStockItemsBetweenDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT p.id AS productId, p.name AS productName, SUM(s.quantityOnHand - s.reservedQuantity) AS availableStock " +
            "FROM StockItem s " +
            "JOIN s.product p " +
            "JOIN s.warehouse w " +
            "WHERE w.active = true " +
            "GROUP BY p.id, p.name, p.reorderPoint " +
            "HAVING SUM(s.quantityOnHand - s.reservedQuantity) <= p.reorderPoint"
    )
    Page<StockAvailability> getLowStockReport(Pageable pageable);

    @Query("SELECT p.id AS productId, p.name AS productName, SUM(s.quantityOnHand - s.reservedQuantity) AS availableStock " +
            "FROM StockItem s " +
            "JOIN s.product p " +
            "JOIN s.warehouse w " +
            "WHERE p.id = :productId " +
            "AND w.active = true " +
            "GROUP BY p.id, p.name, p.reorderPoint " +
            "HAVING SUM(s.quantityOnHand - s.reservedQuantity) <= p.reorderPoint"
    )
    Optional<StockAvailability> getLowStockAlertForProduct(@Param("productId") UUID productId);

    @Query("SELECT p.name AS productName, " +
                "p.id AS productId, " +
                "w.id AS warehouseId, " +
                "w.name AS warehouseName, " +
                "(s.quantityOnHand - s.reservedQuantity) AS availableStock " +
            "FROM StockItem s " +
            "JOIN s.product p " +
            "JOIN s.warehouse w " +
            "WHERE p.id = :productId " +
            "AND w.id = :warehouseId " +
            "AND w.active = true " +
            "AND (s.quantityOnHand - s.reservedQuantity) <= p.reorderPoint"
    )
    Optional<StockAvailabilityByWarehouse> getLowStockReportForProductAndWarehouse(
            @Param("productId") UUID productId,
            @Param("warehouseId") UUID warehouseId
    );

    @Query("SELECT p.name AS productName, " +
                "p.id AS productId," +
                "w.name AS warehouseName," +
                "w.id AS warehouseId," +
                " p.unitCost AS productUnitCost, " +
                "s.quantityOnHand AS quantityOnHand, " +
                "(p.unitCost * s.quantityOnHand) AS inventoryValue " +
            "FROM StockItem s " +
            "JOIN s.product p " +
            "JOIN s.warehouse w " +
            "WHERE p.id = :productId " +
            "AND w.id = :warehouseId "

    )
    InventoryValueByWarehouse getInventoryValueForProductAndWarehouse(
            @Param("productId") UUID productId,
            @Param("warehouseId") UUID warehouseId
    );

    @Query("SELECT p.name AS productName, " +
                "p.id AS productId, " +
                "p.unitCost AS productUnitCost, " +
                "SUM(s.quantityOnHand) AS quantityOnHand, " +
                "SUM(p.unitCost * s.quantityOnHand) AS inventoryValue " +
            "FROM StockItem s " +
            "JOIN s.product p " +
            "GROUP BY p.id, p.name, p.unitCost "
    )
    Page<InventoryValue> getGlobalInventoryValue(Pageable pageable);

}
