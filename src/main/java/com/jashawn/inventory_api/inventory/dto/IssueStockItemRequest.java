package com.jashawn.inventory_api.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.UUID;

@Builder
public record IssueStockItemRequest(
        @Schema(description = "Product UUID being issued out of inventory.")
        @NotNull UUID productId,
        @Schema(description = "Warehouse UUID issuing the inventory.")
        @NotNull UUID warehouseId,
        @Schema(description = "Employee UUID performing the issue operation.")
        @NotNull UUID employeeId,
        @Schema(description = "Department UUID receiving or consuming the issued inventory.")
        @NotNull UUID receivingDepartmentId,
        @Schema(description = "Quantity to remove from available stock.", example = "5")
        @Positive int quantity,
        @Schema(description = "Business reason for the movement.", example = "Department supply request")
        @NotBlank String reason,
        @Schema(description = "External or internal reference for the movement.", example = "REQ-2042")
        @NotBlank String reference
) {}
