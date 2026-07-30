package com.jashawn.inventory_api.inventory.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ManualAdjustmentRequest(UUID productId,
                                      UUID warehouseId,
                                      UUID optionalDepartmentId,
                                      UUID performedByEmployeeId,
                                      int quantity,
                                      String reason,
                                      String reference) {
}
