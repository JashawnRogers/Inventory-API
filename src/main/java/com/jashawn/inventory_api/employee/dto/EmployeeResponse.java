package com.jashawn.inventory_api.employee.dto;

import java.util.UUID;

public record EmployeeResponse(UUID id,
                               String firstName,
                               String lastName,
                               String fullName,
                               String email,
                               UUID departmentId,
                               boolean active) {
}
