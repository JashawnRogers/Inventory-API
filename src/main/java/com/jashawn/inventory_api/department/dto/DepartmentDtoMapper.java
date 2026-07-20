package com.jashawn.inventory_api.department.dto;

import com.jashawn.inventory_api.department.Department;

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
}
