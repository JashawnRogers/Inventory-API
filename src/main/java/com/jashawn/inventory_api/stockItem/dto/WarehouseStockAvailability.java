package com.jashawn.inventory_api.stockItem.dto;

public interface WarehouseStockAvailability {
    String getProductName();
    long getAvailableStock();
    String getWarehouseName();
}
