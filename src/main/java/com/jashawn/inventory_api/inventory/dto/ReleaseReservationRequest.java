package com.jashawn.inventory_api.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ReleaseReservationRequest(
        @NotNull UUID performedByEmployeeId,
        @NotNull UUID productId,
        @NotNull UUID warehouseId,
        @NotNull UUID releasedToDepartmentId,
        @Positive int quantityReleased,
        @NotBlank String reason,
        @NotBlank String reference
) {

}
