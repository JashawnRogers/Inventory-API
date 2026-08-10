package com.jashawn.inventory_api.stockItem.dto;

import java.math.BigDecimal;

public interface InventoryValueByWarehouse {
    String getProductName();
    String getWarehouseName();
    BigDecimal getProductUnitCost();
    Long getQuantityOnHand();
    BigDecimal getInventoryValue();
}
