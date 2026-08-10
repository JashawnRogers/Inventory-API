package com.jashawn.inventory_api.stockMovement;

import com.jashawn.inventory_api.stockMovement.dto.DepartmentCostReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

    @Query("SELECT sm FROM StockMovement sm WHERE sm.createdAt BETWEEN :startDate AND :endDate")
    List<StockMovement> movementHistoryWithinDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT sm FROM StockMovement am WHERE sm.department.id = :department")
    List<StockMovement> findByDepartment(@Param("departmentId") UUID departmentId);

    @Query("SELECT d.name AS departmentName, SUM(sm.totalCost) AS totalCost " +
            "FROM stock_movement sm " +
            "JOIN sm.department d " +
            "WHERE sm.department IS NOT NULL " +
            "AND sm.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY d.id, d.name"
    )
    List<DepartmentCostReport> getDepartmentCostsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
