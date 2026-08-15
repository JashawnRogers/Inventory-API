package com.jashawn.inventory_api.inventory.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface InventoryValueByWarehouse {
    String getProductName();
    UUID getProductId();
    String getWarehouseName();
    UUID getWarehouseId();
    BigDecimal getProductUnitCost();
    Long getQuantityOnHand();
    BigDecimal getInventoryValue();
}
