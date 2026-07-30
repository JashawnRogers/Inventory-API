package com.jashawn.inventory_api.inventory.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ReleaseReservationRequest(UUID performedByEmployeeId,
                                        UUID productId,
                                        UUID warehouseId,
                                        UUID releasedToDepartmentId,
                                        int quantityReleased,
                                        String reason,
                                        String reference) {

}
