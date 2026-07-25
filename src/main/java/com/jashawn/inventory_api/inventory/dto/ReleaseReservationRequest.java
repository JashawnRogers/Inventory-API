package com.jashawn.inventory_api.inventory.dto;

import java.util.UUID;

public record ReleaseReservationRequest(UUID performedByEmployeeId,
                                        UUID productId,
                                        UUID warehouseId,
                                        UUID releasedToDepartmentId,
                                        int quantityReleased,
                                        String reason,
                                        String reference) {

}
