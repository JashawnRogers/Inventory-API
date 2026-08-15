package com.jashawn.inventory_api.stockItem.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StockItemRequest(
        @NotNull(message = "Must provide product ID")
        UUID productId,

        @NotNull(message = "Must provide warehouse ID")
        UUID warehouseId
) {
}
