package com.jashawn.inventory_api.inventory.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record InventoryValueByWarehouseResponse(
        String productName,
        UUID productId,
        BigDecimal inventoryValue,
        BigDecimal productUnitCost,
        Long quantityOnHand,
        String warehouseName,
        UUID warehouseId
) {
}
