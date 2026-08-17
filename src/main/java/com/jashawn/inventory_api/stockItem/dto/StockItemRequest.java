package com.jashawn.inventory_api.stockItem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record StockItemRequest(
        @NotNull(message = "Must provide product ID")
        @Schema(description = "Product UUID for the stock item report.")
        UUID productId,

        @NotNull(message = "Must provide warehouse ID")
        @Schema(description = "Warehouse UUID for the stock item report.")
        UUID warehouseId
) {
}
