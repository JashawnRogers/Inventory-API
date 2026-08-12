package com.jashawn.inventory_api.employee.dto;

import java.util.UUID;

public record EmployeeSummary(UUID id, String fullName, String departmentCode) {
}
