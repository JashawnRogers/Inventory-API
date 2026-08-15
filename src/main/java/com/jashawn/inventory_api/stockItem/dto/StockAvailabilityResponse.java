package com.jashawn.inventory_api.stockItem.dto;

import java.util.UUID;

public record StockAvailabilityResponse(
        UUID id,
        String name,
        Long availableStock
) {
}
