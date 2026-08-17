package com.jashawn.inventory_api.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record ReceiveInventoryRequest(
        @Schema(description = "Product UUID being received into inventory.")
        UUID productId,

        @Schema(description = "Warehouse UUID receiving the inventory.")
        UUID warehouseId,

        @Schema(description = "Employee UUID performing the receive operation.")
        UUID employeeId,

        @Schema(description = "Quantity to add to stock on hand.", example = "10")
        int quantity,

        @Schema(description = "External or internal reference for the movement.", example = "PO-1001")
        String reference,

        @Schema(description = "Business reason for the movement.", example = "Initial purchase order receipt")
        String reason) {
}
