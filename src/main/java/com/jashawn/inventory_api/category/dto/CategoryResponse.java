package com.jashawn.inventory_api.category.dto;

import com.jashawn.inventory_api.product.dto.ProductResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String description,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ProductResponse> products
) {}
