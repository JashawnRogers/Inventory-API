package com.jashawn.inventory_api.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ReserveStockItemRequest(
        @Schema(description = "Employee UUID performing the reservation.")
        @NotNull UUID performedByEmployeeId,
        @Schema(description = "Product UUID being reserved.")
        @NotNull UUID productId,
        @Schema(description = "Warehouse UUID holding the stock being reserved.")
        @NotNull UUID warehouseId,
        @Schema(description = "Department UUID the stock is reserved for.")
        @NotNull UUID reservedForDepartmentId,
        @Schema(description = "Quantity to move from available stock into reserved stock.", example = "3")
        @Positive int quantityReserved,
        @Schema(description = "Business reason for the movement.", example = "Planned department usage")
        @NotBlank String reason,
        @Schema(description = "External or internal reference for the movement.", example = "RSV-3001")
        @NotBlank String reference
) {
}
