package com.jashawn.inventory_api.inventory.dto;

import java.util.UUID;

public record WarehouseTransferRequest(UUID productId,
                                       UUID issuingWarehouseId,
                                       UUID receivingWarehouseId,
                                       UUID performedByEmployeeId,
                                       int quantity,
                                       String reason,
                                       String reference) {
}
