package com.jashawn.inventory_api.inventory.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record InventoryValueResponse(
        String productName,
        UUID productId,
        Long quantityOnHand,
        BigDecimal productUnitCost,
        BigDecimal inventoryValue
) {
}
