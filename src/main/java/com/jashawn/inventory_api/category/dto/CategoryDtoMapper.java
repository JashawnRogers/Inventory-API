package com.jashawn.inventory_api.category.dto;

import com.jashawn.inventory_api.category.Category;

public class CategoryDtoMapper {

    public static CategoryResponse toDto(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
