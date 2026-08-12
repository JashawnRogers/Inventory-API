package com.jashawn.inventory_api.stockMovement;

import com.jashawn.inventory_api.stockMovement.dto.DepartmentCostReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

    @Query("SELECT sm FROM StockMovement sm WHERE sm.createdAt BETWEEN :startDate AND :endDate")
    Page<StockMovement> movementHistoryWithinDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT sm " +
            "FROM StockMovement sm " +
            "WHERE sm.department.id = :departmentId " +
            "AND sm.createdAt BETWEEN :startDate AND :endDate"
    )
    Page<StockMovement> findByDepartment(
            @Param("departmentId") UUID departmentId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT d.id AS departmentId, " +
                "d.name AS departmentName, " +
                "d.code AS departmentCode, " +
                "SUM(sm.totalCost) AS totalCost " +
            "FROM StockMovement sm " +
            "JOIN sm.department d " +
            "WHERE sm.department IS NOT NULL " +
            "AND sm.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY d.id, d.name, d.code"
    )
    Page<DepartmentCostReport> getDepartmentCostsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
