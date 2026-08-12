package com.jashawn.inventory_api.employee.dto;

import com.jashawn.inventory_api.employee.Employee;

public class EmployeeDtoMapper {

    public static EmployeeResponse toDto(Employee employee) {
        return new EmployeeResponse(employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getDepartment().getId(),
                employee.isActive()
        );
    }

    public static EmployeeSummary toSummary(Employee employee) {
        return new EmployeeSummary(employee.getId(), employee.getFullName(), employee.getDepartment().getCode());
    }
}
