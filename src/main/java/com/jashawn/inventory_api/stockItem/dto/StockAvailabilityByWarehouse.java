package com.jashawn.inventory_api.stockItem.dto;

public interface StockAvailabilityByWarehouse {
    String getProductName();
    String getWarehouseName();
    Long getAvailableStock();

}
