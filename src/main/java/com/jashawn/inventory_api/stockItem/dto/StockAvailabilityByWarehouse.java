package com.jashawn.inventory_api.stockItem.dto;

import java.util.UUID;

public interface StockAvailabilityByWarehouse {
    UUID getProductId();
    String getProductName();
    UUID getWarehouseId();
    String getWarehouseName();
    Long getAvailableStock();

}
