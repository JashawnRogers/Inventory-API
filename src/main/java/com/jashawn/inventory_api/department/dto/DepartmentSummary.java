package com.jashawn.inventory_api.department.dto;

import java.util.UUID;

public record DepartmentSummary(UUID id, String name, String code) {
}
