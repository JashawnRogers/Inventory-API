package com.jashawn.inventory_api.department.dto;

import com.jashawn.inventory_api.department.Department;
import com.jashawn.inventory_api.reports.dto.DepartmentCostResponse;
import com.jashawn.inventory_api.stockMovement.dto.DepartmentCostReport;

public class DepartmentDtoMapper {

    public static DepartmentResponse toDto(Department department) {
        return new DepartmentResponse(
                department.getId(),
                department.getName(),
                department.getCode(),
                department.isActive(),
                department.getCreatedAt(),
                department.getUpdatedAt(),
                department.getDeletedAt()
        );
    }

    public static DepartmentSummary toSummary(Department department) {
        return new DepartmentSummary(department.getId(), department.getName(), department.getCode());
    }

    public static DepartmentCostResponse toCostResponse(DepartmentCostReport d) {
        return new DepartmentCostResponse(
                d.getDepartmentId(),
                d.getDepartmentName(),
                d.getDepartmentCode(),
                d.getTotalCost()
        );
    }
}
