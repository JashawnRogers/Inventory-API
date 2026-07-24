package com.jashawn.inventory_api.inventory.dto;

import java.util.UUID;

public record ReserveStockItemRequest(UUID performedByEmployeeId,
                                      UUID productId,
                                      UUID warehouseId,
                                      UUID reservedForDepartmentId,
                                      int quantityReserved,
                                      String reason,
                                      String reference) {
}
