package com.jashawn.inventory_api.inventory.dto;

import java.util.UUID;

public record IncreaseByAdjustmentRequest(UUID productId,
                                          UUID warehouseId,
                                          UUID optionalDepartmentId,
                                          UUID performedByEmployeeId,
                                          int quantity,
                                          String reason,
                                          String reference) {
}
