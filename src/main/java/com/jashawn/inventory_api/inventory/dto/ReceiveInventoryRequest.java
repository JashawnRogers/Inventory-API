package com.jashawn.inventory_api.inventory.dto;

import java.util.UUID;

public record ReceiveInventoryRequest(UUID productId,
                                      UUID warehouseId,
                                      UUID employeeId,
                                      int quantity,
                                      String reference,
                                      String reason) {
}
