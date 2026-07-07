package com.jashawn.inventory_api.product.dto;

import com.jashawn.inventory_api.category.dto.CategoryResponse;
import com.jashawn.inventory_api.supplier.dto.SupplierResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        UUID sku,
        String name,
        String description,
        BigDecimal unitCost,
        int reorderPoint,
        boolean isActive,
        UUID categoryId,
        UUID supplierId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
