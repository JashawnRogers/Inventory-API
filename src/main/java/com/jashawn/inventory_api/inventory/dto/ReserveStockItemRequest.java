package com.jashawn.inventory_api.inventory.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ReserveStockItemRequest(UUID performedByEmployeeId,
                                      UUID productId,
                                      UUID warehouseId,
                                      UUID reservedForDepartmentId,
                                      int quantityReserved,
                                      String reason,
                                      String reference) {
}
