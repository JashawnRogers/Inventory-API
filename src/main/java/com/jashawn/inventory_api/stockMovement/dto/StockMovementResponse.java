package com.jashawn.inventory_api.stockMovement.dto;

import com.jashawn.inventory_api.department.dto.DepartmentSummary;
import com.jashawn.inventory_api.employee.dto.EmployeeSummary;
import com.jashawn.inventory_api.product.dto.ProductSummary;
import com.jashawn.inventory_api.stockMovement.MovementType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record StockMovementResponse(
        UUID id,
        ProductSummary product,
        EmployeeSummary employee,
        DepartmentSummary movedToDepartment,
        MovementType movementType,
        int quantity,
        BigDecimal totalCost,
        String reason,
        String reference,
        LocalDateTime createdAt
) {
}
