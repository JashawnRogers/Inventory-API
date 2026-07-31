package com.jashawn.inventory_api.inventory.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record WarehouseTransferRequest(UUID productId,
                                       UUID issuingWarehouseId,
                                       UUID receivingWarehouseId,
                                       UUID performedByEmployeeId,
                                       int quantity,
                                       String reason,
                                       String reference) {
}
