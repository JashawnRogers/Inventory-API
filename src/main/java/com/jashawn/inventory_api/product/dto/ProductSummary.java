package com.jashawn.inventory_api.product.dto;

import java.util.UUID;

public record ProductSummary(UUID id,
                             String name,
                             String sku) {
}
