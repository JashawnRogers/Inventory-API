package com.jashawn.inventory_api.category.dto;

public record UpdateCategoryRequest(String name, String description, Boolean status) {
}
