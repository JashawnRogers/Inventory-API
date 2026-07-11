package com.jashawn.inventory_api.category.dto;

import com.jashawn.inventory_api.product.dto.ProductResponse;

import java.util.List;

public record CreateCategoryRequest(
        String name,
        String description,
        List<ProductResponse> products
) {
}
