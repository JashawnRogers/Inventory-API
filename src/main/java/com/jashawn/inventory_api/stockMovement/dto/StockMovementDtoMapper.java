package com.jashawn.inventory_api.stockMovement.dto;

import com.jashawn.inventory_api.department.dto.DepartmentDtoMapper;
import com.jashawn.inventory_api.department.dto.DepartmentSummary;
import com.jashawn.inventory_api.employee.dto.EmployeeDtoMapper;
import com.jashawn.inventory_api.employee.dto.EmployeeSummary;
import com.jashawn.inventory_api.product.dto.ProductDtoMapper;
import com.jashawn.inventory_api.product.dto.ProductSummary;
import com.jashawn.inventory_api.stockMovement.StockMovement;

public class StockMovementDtoMapper {

    public static StockMovementResponse toDto(StockMovement sm) {
        ProductSummary productSummary = ProductDtoMapper.toSummaryDto(sm.getStockItem().getProduct());
        EmployeeSummary employeeSummary = EmployeeDtoMapper.toSummary(sm.getEmployee());

        DepartmentSummary departmentSummary = null;
        if (sm.getDepartment() != null) {
            departmentSummary = DepartmentDtoMapper.toSummary(sm.getDepartment());
        }

        return new StockMovementResponse(
                sm.getId(),
                productSummary,
                employeeSummary,
                departmentSummary,
                sm.getMovementType(),
                sm.getQuantity(),
                sm.getTotalCost(),
                sm.getReason(),
                sm.getReference(),
                sm.getCreatedAt()
        );
    }
}
