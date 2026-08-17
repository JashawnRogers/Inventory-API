package com.jashawn.inventory_api.inventory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ReleaseReservationRequest(
        @Schema(description = "Employee UUID performing the release.")
        @NotNull UUID performedByEmployeeId,
        @Schema(description = "Product UUID whose reserved stock is being released.")
        @NotNull UUID productId,
        @Schema(description = "Warehouse UUID holding the reserved stock.")
        @NotNull UUID warehouseId,
        @Schema(description = "Department UUID the reservation is released from or to in the audit record.")
        @NotNull UUID releasedToDepartmentId,
        @Schema(description = "Quantity to move from reserved stock back to available stock.", example = "2")
        @Positive int quantityReleased,
        @Schema(description = "Business reason for the movement.", example = "Reservation no longer needed")
        @NotBlank String reason,
        @Schema(description = "External or internal reference for the movement.", example = "RSV-3001")
        @NotBlank String reference
) {

}
