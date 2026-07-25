package com.jashawn.inventory_api.inventory.dto;

import java.util.UUID;

public record ManualAdjustmentRequest(UUID productId,
                                      UUID warehouseId,
                                      UUID optionalDepartmentId,
                                      UUID performedByEmployeeId,
                                      int quantity,
                                      String reason,
                                      String reference) {
}
