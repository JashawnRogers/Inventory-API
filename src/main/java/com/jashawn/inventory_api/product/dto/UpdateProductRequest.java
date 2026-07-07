package com.jashawn.inventory_api.product.dto;

import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductRequest(
        @Nullable String name,
        @Nullable String description,
        @Nullable BigDecimal unitCost,
        @Nullable Integer reorderPoint,
        @Nullable UUID categoryId,
        @Nullable UUID supplierId
        ) {}
