package com.jashawn.inventory_api.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.UUID;

@Builder
public record WarehouseTransferRequest(
        @Schema(description = "Product UUID being transferred.")
        @NotNull UUID productId,
        @Schema(description = "Warehouse UUID issuing the stock.")
        @NotNull UUID issuingWarehouseId,
        @Schema(description = "Warehouse UUID receiving the stock.")
        @NotNull UUID receivingWarehouseId,
        @Schema(description = "Employee UUID performing the transfer.")
        @NotNull UUID performedByEmployeeId,
        @Schema(description = "Quantity to transfer between warehouses.", example = "8")
        @Positive int quantity,
        @Schema(description = "Business reason for the transfer.", example = "Warehouse replenishment")
        @NotBlank String reason,
        @Schema(description = "External or internal reference for the movement.", example = "TRN-7788")
        @NotBlank String reference
) {
}
