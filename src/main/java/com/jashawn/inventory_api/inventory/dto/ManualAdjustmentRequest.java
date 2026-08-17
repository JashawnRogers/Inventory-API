package com.jashawn.inventory_api.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ManualAdjustmentRequest(
        @Schema(description = "Product UUID being adjusted.")
        @NotNull UUID productId,
        @Schema(description = "Warehouse UUID whose stock item is being adjusted.")
        @NotNull UUID warehouseId,
        @Schema(description = "Department UUID to attach to the adjustment audit record.")
        @NotNull UUID optionalDepartmentId,
        @Schema(description = "Employee UUID performing the adjustment.")
        @NotNull UUID performedByEmployeeId,
        @Schema(description = "Quantity to increase or decrease.", example = "4")
        @Positive int quantity,
        @Schema(description = "Business reason for the adjustment.", example = "Cycle count correction")
        @NotBlank String reason,
        @Schema(description = "External or internal reference for the movement.", example = "ADJ-9090")
        @NotBlank String reference
) {
}
