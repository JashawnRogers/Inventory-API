package com.jashawn.inventory_api.department.dto;

public record UpdateDepartmentRequest(String name, String code, Boolean active) {
}
