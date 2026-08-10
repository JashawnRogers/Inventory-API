package com.jashawn.inventory_api.stockItem.dto;

import java.math.BigDecimal;

public interface InventoryValue {
    String getProductName();
    Long getQuantityOnHand();
    BigDecimal getProductUnitCost();
    BigDecimal getInventoryValue();
}
