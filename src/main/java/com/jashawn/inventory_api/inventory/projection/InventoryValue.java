package com.jashawn.inventory_api.inventory.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface InventoryValue {
    UUID getProductId();
    String getProductName();
    Long getQuantityOnHand();
    BigDecimal getProductUnitCost();
    BigDecimal getInventoryValue();
}
