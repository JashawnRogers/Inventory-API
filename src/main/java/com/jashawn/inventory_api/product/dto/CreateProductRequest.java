package com.jashawn.inventory_api.product.dto;


import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductRequest(
        String name,
        String description,
        BigDecimal unitCost,
        Integer reorderPoint,
        UUID categoryId,
        UUID supplierId
) {
}
